package stallone.io;

import stallone.doubles.fastutils.LongArrayList;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import stallone.io.CachedRandomAccessFile;

/**
 * Fast reader for asci text files which contain text data in a line oriented way.
 *
 * @author  Martin Senne
 */
public class CachedAsciiFileReader
{
    /** */
    public static Pattern commentLine = Pattern.compile("[;#].*|\\s*");
    /** */
    public static Pattern whiteSpacePattern = Pattern.compile("\\s+");
    /** Logger. */
    private static final Logger log = Logger.getLogger(CachedAsciiFileReader.class.getName());
    /** Filename of the file we are working with. */
    protected String filename;
    /** For fast and efficient file access. */
    private CachedRandomAccessFile randomAccessFile;
    /** Number of lines this file has, which do not match commentLine pattern. */
    private int relevantLines;
    /** Byte offsets of line starts in file. */
    private long[] lineStartOffsets;
    /** Pattern, which actually is used to determine comment lines */
    private Pattern usedCommentPattern;

    public CachedAsciiFileReader()
    {}
    
    /**
     * Fast reader for asci files.
     *
     * @param   filename  is the file to open for reading.
     *
     * @throws  FileNotFoundException
     * @throws  IOException
     */
    public CachedAsciiFileReader(final String filename) throws FileNotFoundException, IOException
    {
        this(filename, null);
    }

    /**
     * Fast reader for asci files. Lines matching the commentPattern are automatically discarded.
     *
     * @param   filename        is the file to open for reading.
     * @param   commentPattern  lines to automatically discard
     *
     * @throws  FileNotFoundException
     * @throws  IOException
     */
    public CachedAsciiFileReader(final String filename, final Pattern commentPattern) throws FileNotFoundException,
            IOException
    {
        this.filename = filename;
        this.usedCommentPattern = commentPattern;

        randomAccessFile = new CachedRandomAccessFile(filename);

        this.lineStartOffsets = null;
    }
    
    public void setFilename(final String _filename)
    {
        this.filename = _filename;
    }
    
    public void setCommentPattern(final Pattern _commentPattern)
    {
        this.usedCommentPattern = _commentPattern;
    }
    
    public void open()
            throws IOException
    {
        if (randomAccessFile != null)
            randomAccessFile.close();
        randomAccessFile = new CachedRandomAccessFile(filename);
    }

    /**
     * Determine byte offset of each line in the file.
     * Calls {@link #evaluateLineWhileScanning(java.lang.String, int) } which can be overriden in subclasses
     *   as a quasi-callback.
     * 
     * This method MUST be called before invoking {@link #getLine(int) } or {@link #getNumberOfLines() }
     * 
     */
    public void scan()
            throws IOException
    {
        final int INITIAL_SIZE = 1000000;

        final LongArrayList lineOffsets = new LongArrayList(INITIAL_SIZE);

            long currentPos = 0;
            int currentLineNumber = 0;
            long oldPos;
            String textline;

            while ((textline = randomAccessFile.readLine()) != null)
            {
                // save old position
                oldPos = currentPos;

                // if commentPattern active, then read away comment lines
                if (usedCommentPattern != null)
                {
//                    while (usedCommentPattern.matcher(textline).matches()) {
//                        textline = randomAccessFile.readLine(); // skip line
//                    }
                    if (usedCommentPattern.matcher(textline).matches())
                    {
                        continue;
                    }
                }

                scanLine(textline, currentLineNumber);

                currentLineNumber++;
                currentPos = randomAccessFile.getFilePointer();
                lineOffsets.add(oldPos);

                // garbage collection is one solution
                // if ( (currentLineNumber % 1000000) == 0) {
                //    System.out.println("Scanned up to line: " + currentLineNumber);
                //    System.gc();
                // }
            }
            scanEnd(currentLineNumber);

            lineOffsets.trim();

        // determine line offsets
        lineStartOffsets = lineOffsets.toLongArray();
        relevantLines = lineStartOffsets.length;
    }

    /**
     * Determine number of lines this file has.
     * Lines matching the comment pattern do not count as lines.
     *
     * @see #scan()
     * @return
     */
    public int getNumberOfLines()
    {
        return relevantLines;
    }

    
    /**
     * Get line with line number <code>lineNumber</code>.
     *
     * @param lineNumber  to get
     * @return String of requested line.
     *
     * @see #scan()
     */
    public String getLine(final int lineNumber)
    {
        if (lineStartOffsets == null)
        {
            throw new RuntimeException("No line offsets available, you need to call scan() first.");
        }

        try
        {
            if ((0 <= lineNumber) && (lineNumber < relevantLines))
            {
                randomAccessFile.seek(lineStartOffsets[lineNumber]);
                String line = randomAccessFile.readLine();
                return line;
            }
            else
            {
                throw new IllegalArgumentException("Requested line " + lineNumber + " is out of scope.");
            }
        } catch (IOException ex)
        {
            Logger.getLogger(CachedAsciiFileReader.class.getName()).log(Level.SEVERE, "I/O error while requesting line "
                    + lineNumber + " of file '" + filename + "'.", ex);
            throw new RuntimeException("I/O error while requesting line "
                    + lineNumber + " of file '" + filename + "'.");
        }
    }

    public void close() throws IOException
    {
        randomAccessFile.close();
    }

    /**
     * Passes the line to the analyzer while scanning.
     * @param textline
     * @param currentLineNumber
     * @return true if the line is accepted, false if rejected.
     */
    protected boolean scanLine(String textline, int currentLineNumber)
    {
        return true;
    }

    protected void scanEnd(int currentLineNumber)
    {
        // do nothing
    }
}
