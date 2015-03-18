package edu.nl.ru.analysis;

import edu.nl.ru.linalg.Matrix;
import edu.nl.ru.linalg.WelchOutputType;
import edu.nl.ru.miscellaneous.Windows;
import nl.fcdonders.fieldtrip.bufferclient.BufferClientClock;
import nl.fcdonders.fieldtrip.bufferclient.BufferEvent;
import nl.fcdonders.fieldtrip.bufferclient.Header;
import nl.fcdonders.fieldtrip.bufferclient.SamplesEventsCount;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pieter on 23-2-2015.
 */
public class ContinuousClassifier implements Runnable {

    private static Logger log = Logger.getLogger(ContinuousClassifier.class);
    private String bufferHost, endValue, predictionEventType, endType;
    private int bufferPort, timeoutMs;
    private double overlap, predictionFilter;
    private int sampleTrialLength, sampleStep, nSamples;
    private Header header;
    private List<Classifier> classifiers;

    public ContinuousClassifier(String bufferHost, int bufferPort, Header header, String endType, String endValue,
                                String predictionEventType, int lengthTrSample, double overlap, int timeoutMs, List<Classifier> classifiers, double predictionFilter) {
        log.setLevel(Level.DEBUG);
        this.bufferHost = bufferHost;
        this.bufferPort = bufferPort;
        this.header = header;
        this.endType = endType;
        this.endValue = endValue;
        this.predictionEventType = predictionEventType;
        this.sampleTrialLength = lengthTrSample;
        this.overlap = overlap;
        this.timeoutMs = timeoutMs;
        this.classifiers = classifiers;
        this.predictionFilter = predictionFilter;

        // Compute parameters
        this.sampleStep = (int) Math.round(sampleTrialLength * this.overlap);
        this.nSamples = -1;
    }


    public static void main(String[] args) {
        double badChannelThreshold = -1.;
        double badTrialThreshold = -1.;
        int[] timeIdx = new int[]{0, 1, 2};
        int[] freqIdx = new int[]{0, 1};
        Matrix W = Matrix.zeros(2, 2);
        RealVector b = Matrix.zeros(2, 1).getColumnVector(0);
        double[] startMs = new double[]{0};
        Classifier classifier = new Classifier(W, b, true, badChannelThreshold, badTrialThreshold, Windows.WindowType
                .HANNING, WelchOutputType.AMPLITUDE, timeIdx, freqIdx, 1, null, null, 2, 100, startMs);
        List<Classifier> classifiers = new LinkedList<Classifier>();
        classifiers.add(classifier);
        ContinuousClassifier c = new ContinuousClassifier("localhost", 1973, null, "stimulus.test", "end",
                "classifiers.prediction", 25, .5, 1000, classifiers, 1.);
        Thread t = new Thread(c);
        t.start();
    }

    @Override
    public void run() {
        log.info("Running the dataprocessor");

        BufferClientClock C = new BufferClientClock();

        while (header == null) {
            try {
                log.info("Connecting to " + bufferHost + ":" + bufferPort);
                C.connect(bufferHost, bufferPort);
                //C.setAutoReconnect(true);
                if (C.isConnected()) {
                    header = C.getHeader();
                }
            } catch (IOException e) {
                header = null;
            }
            if (header == null) {
                log.warn("Invalid Header... waiting");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //float[][] data = C.getFloatData(0,header.nSamples-1);
        log.info("#channels....: " + header.nChans);
        log.info("#samples.....: " + header.nSamples);
        log.info("#events......: " + header.nEvents);
        log.info("Sampling Freq: " + header.fSample);
        log.info("data type....: " + header.dataType);
        for (int n = 0; n < header.nChans; n++) {
            if (header.labels[n] != null) {
                log.debug("Ch. " + n + ": " + header.labels[n]);
            }
        }
        nSamples = header.nSamples;

        // Now do the echo-server
        int nEvents = header.nEvents, nEpochs = 0;
        boolean endExpt = false;
        long t0 = 0;
        Matrix dv = Matrix.zeros(25, 2);
        while (!endExpt) {
            // Getting data from buffer
            SamplesEventsCount status = null; // Block until there are new events
            try {
                status = C.waitForSamples(this.nSamples + sampleTrialLength, timeoutMs);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (status.nSamples < header.nSamples) {
                log.info("Buffer restart detected");
                nSamples = status.nSamples;
                dv = Matrix.zeros(25, 2);
                continue;
            }

            // Logging stuff when nothing is happening
            if (System.currentTimeMillis() - t0 > 5000) {
                log.info(String.format("5.3f seconds, %d samples, %d events", System.currentTimeMillis() / 1000, status.nSamples, status.nEvents));
                t0 = System.currentTimeMillis();
            }

            // Process any new data
            int onSamples = nSamples;
            int[] start = Matrix.range(onSamples, status.nSamples - sampleTrialLength - 1,sampleStep);
            if (start.length > 0)
                nSamples = start[start.length-1] + sampleStep;

            for (int startId = 0; startId < start.length; startId++) {
                nEpochs++;

                // Get the data
                int from = start[startId];
                int to = start[startId] + sampleTrialLength - 1;
                double[][] data = null;
                try {
                    data = C.getDoubleData(from, to);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.debug(String.format("Got data @ %d->%d samples", from, to));

                // Apply classification
                // TODO apply each classifier
                ClassifierResult classifierResult = classifiers.get(0).apply(new Matrix(data));
//                log.debug(String.format("Classifier result: %s", classifierResult.p));

                // Smooth the classifiers
                dv = new Matrix(dv.scalarMultiply(predictionFilter).add(classifierResult.f.scalarMultiply(1. - predictionFilter)));

                // Send prediction event
                BufferEvent event = new BufferEvent(predictionEventType, dv.getRow(0), from);
                try {
                    C.putEvent(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.debug(String.format("%d) Classifier pred: %s", from, Arrays.toString(dv.getRow(0))));
            }

            // Deal with events
            if (status.nEvents > nEvents) {
                BufferEvent[] events = null;
                try {
                    events = C.getEvents(nEvents, status.nEvents - 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean any = false;
                for (BufferEvent event : events) {
                    any = any || (event.getType().getArray().equals(endType) && event.getValue().getArray().equals(endValue));
                    log.debug("Received " + event.getType() + " event: " + event.getValue());
                }
                if (any) {
                    log.info("Got exit event. Stoppping");
                    endExpt = true;
                }
                nEvents = status.nEvents;
            }
        }
        try {
            C.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
