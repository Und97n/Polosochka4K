package com.polosochka;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Special class for transferring IO data to many writers.
 * <br><br>
 *
 * @author Philips Xi-Qing
 * Created 2021-09-15 20:27
 */
public class MultiWriter extends Writer {
    private final List<Writer> writers;

    public MultiWriter(Collection<Writer> writers) {
        this.writers = new ArrayList<>(writers);
    }

    public MultiWriter(Writer... writers) {
        this.writers = new ArrayList<>();

        Collections.addAll(this.writers, writers);
    }

    @FunctionalInterface
    public interface WriterAction {
        void apply(Writer w) throws IOException;
    }

    private synchronized void iterateWriters(WriterAction action) throws MultiWriterIOException {
        List<IOException> exceptions = null;

        for (Writer writer : writers) {
            try {
                action.apply(writer);
            } catch (IOException e) {
                if (exceptions == null) {
                    exceptions = new ArrayList<>();
                }
                // We don't want to ignore other writers in some cases
                exceptions.add(e);
            }
        }

        if (exceptions != null) {
            throw new MultiWriterIOException(exceptions);
        }
    }

    @Override
    public synchronized void write(char[] cbuf, int off, int len) throws IOException {
        iterateWriters(writer -> writer.write(cbuf, off, len));
    }

    @Override
    public synchronized void flush() throws IOException {
        iterateWriters(Writer::flush);
    }

    @Override
    public synchronized void close() throws IOException {
        iterateWriters(Writer::close);
    }

    public synchronized void addWriter(Writer w) {
        writers.add(w);
    }

    static class MultiWriterIOException extends IOException {
        private final List<IOException> exceptions;

        MultiWriterIOException(List<IOException> exceptions) {
            super(exceptions.stream().map(Throwable::getMessage)
                    .collect(Collectors.joining("\n", "MultiWriterIOException. Causes:\n", "")));
            this.exceptions = exceptions;
        }
    }
}
