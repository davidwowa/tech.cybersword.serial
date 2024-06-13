package tech.cybersword;

import java.security.SecureRandom;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fazecast.jSerialComm.SerialPort;

public class SerialFuzzer {

    private static final Logger logger = LogManager.getLogger(SerialFuzzer.class);

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 9) {
            System.out.println(
                    "Usage: java -jar tech.cybersword.serial-*.jar <serialDevice> <speed_115200> <databits(stndard 8)> <delay_100> <maxByteArraySize> <expectedResponseSize_1024> <useRX false|true> <log true|false> <use enter true|false>");
            System.exit(1);
        }

        if (logger.isInfoEnabled()) {
            logger.info("start serial fuzzing");
        }

        String device = args[0];
        Integer speed = Integer.valueOf(args[1]);
        Integer dataBits = Integer.valueOf(args[2]);
        Integer delay = Integer.valueOf(args[3]);
        Integer maxByteArraySize = Integer.valueOf(args[4]);
        Integer expectedResponseSize = Integer.valueOf(args[5]);
        Boolean useRX = Boolean.valueOf(args[6]);
        Boolean log = Boolean.valueOf(args[7]);
        Boolean useEnter = Boolean.valueOf(args[8]);

        SerialPort serialPort = SerialPort.getCommPort(device);
        serialPort.setComPortParameters(speed, dataBits, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (serialPort.openPort()) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("%s opened successfully.", device));
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("%s not possible to open.", device));
            }
            System.out.println("not possible to open serial device");
            return;
        }

        try {
            Random random = new SecureRandom();
            while (true) {
                int randomByteArraySize = random.nextInt(maxByteArraySize);
                byte[] randomBytes = new byte[randomByteArraySize];

                random.nextBytes(randomBytes);

                serialPort.writeBytes(randomBytes, randomBytes.length);
                if (useEnter) {
                    // "Enter" senden (Neue Zeile)
                    serialPort.writeBytes(new byte[] { '\n' }, 1); // Verwenden Sie '\r\n' für CRLF, falls erforderlich
                }

                if (logger.isInfoEnabled() && log) {
                    logger.info(String.format("TX[%s]: %s", randomByteArraySize, byteArrayToHex(randomBytes)));
                }

                if (useRX) {
                    byte[] readBuffer = new byte[expectedResponseSize];
                    int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
                    String response = new String(readBuffer, 0, numRead);
                    if (logger.isInfoEnabled() && log) {
                        // logger.info(String.format("RX[%s]: %s", numRead, byteArrayToHex(readBuffer, numRead)));
                        logger.info(String.format("RX[%s]: %s", numRead, response));
                    }
                }

                Thread.sleep(delay);
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error in serial communication.", e);
            }
            e.printStackTrace();
        } finally {
            serialPort.closePort();
            if (logger.isInfoEnabled()) {
                logger.info("END");
            }
        }
    }

    public static String byteArrayToHex(byte[] a, int length) {
        StringBuilder sb = new StringBuilder(length * 2);
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x ", a[i]));
        }
        return sb.toString();
    }

    public static String byteArrayToHex(byte[] a) {
        return byteArrayToHex(a, a.length);
    }
}