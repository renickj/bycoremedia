package com.coremedia.blueprint.importer.validation;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.commons.io.output.NullWriter;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public final class JavaScriptValidator {
  private static final Logger LOG = LoggerFactory.getLogger(JavaScriptValidator.class);
  private static final NullWriter NULL_WRITER = new NullWriter();

  // static utility class
  private JavaScriptValidator() {
  }


  // --- features ---------------------------------------------------

  /**
   * Validate JavaScript
   *
   * @param systemId resource name, only for logging
   * @param bytes the JavaScript to be validated
   */
  public static int validateJs(String systemId, InputStream bytes) {
    try {
      Reader reader = new InputStreamReader(bytes);
      ErrorReporterWrapper errorHandler = new ErrorReporterWrapper(systemId);
      JavaScriptCompressor compressor = new JavaScriptCompressor(reader, errorHandler);
      compressor.compress(NULL_WRITER, -1, false, true, true, true);
      return errorHandler.numErrors();
    } catch (IOException e) {
      LOG.error("Error validating JavaScript.", e);
      return 1;
    }
  }


  // --- internal ---------------------------------------------------

  /**
   * An ErrorReporter which only logs on level info, because JS validation is only
   * informational in this context and has no effect on the actual operation.
   */
  private static class ErrorReporterWrapper implements ErrorReporter {
    private String systemId;
    private int errors = 0;

    public ErrorReporterWrapper(String systemId) {
      this.systemId = systemId;
      errors = 0;
    }

    @Override
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
      // intentionally ignore
    }

    @Override
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
      ++errors;
      LOG.error("JS validation error: File {}, Line: {}, LineOffset: {}, LineSource: '{}': {}", new Object[]{systemId, line, lineOffset, lineSource, message});
    }

    @Override
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
      error(message, sourceName, line, lineSource, lineOffset);
      return new EvaluatorException(message);
    }

    public int numErrors() {
      return errors;
    }
  }

}
