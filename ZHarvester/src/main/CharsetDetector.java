package main;

import org.mozilla.universalchardet.UniversalDetector;

public class CharsetDetector {
  public static String Detect(byte[] stream) throws java.io.IOException {
    UniversalDetector detector = new UniversalDetector(null);
    detector.handleData(stream, 0, stream.length);
    detector.dataEnd();
    String encoding = detector.getDetectedCharset();
    detector.reset();
    return encoding;
  }
}