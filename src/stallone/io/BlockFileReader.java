package stallone.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import stallone.api.ints.IntsPrimitive;
import stallone.api.strings.Strings;
import stallone.io.CachedAsciiFileReader;

/**
 *
 * @author Frank Noe
 */
public class BlockFileReader extends CachedAsciiFileReader
{

    private final int WORDTYPE_STRING = 0, WORDTYPE_DOUBLE = 1, WORDTYPE_INT = 2;
    private List<Block> blocks = new ArrayList();
    private Block currentBlock = null;
    // current read
    private int currentLineNumber = -1;
    private String[] currentWords = null;

    public BlockFileReader(final String filename)
            throws FileNotFoundException, IOException
    {
        super(filename);
    }

    /**
     * Passes the line to the analyzer while scanning.
     *
     * @param textline
     * @param currentLineNumber
     * @return true if the line is accepted, false if rejected.
     */
    @Override
    protected boolean scanLine(String textline, int currentLineNumber)
    {
        int[] types = getWordTypes(textline);

        if (currentBlock == null)
        {
            currentBlock = new Block(types, currentLineNumber);
        }
        else
        {
            // is this a new block?
            if (!equal(currentBlock.types, types))
            {
                currentBlock.setLastLine(currentLineNumber - 1);
                blocks.add(currentBlock);
                currentBlock = new Block(types, currentLineNumber);
            }
        }

        return true;
    }

    @Override
    protected void scanEnd(int currentLineNumber)
    {
        currentBlock.setLastLine(currentLineNumber - 1);
        blocks.add(currentBlock);
    }

    private int getWordType(String word)
    {
        if (Strings.util.isInt(word))
        {
            return WORDTYPE_INT;
        }
        if (Strings.util.isDouble(word))
        {
            return WORDTYPE_DOUBLE;
        }
        return WORDTYPE_STRING;
    }

    private int[] getWordTypes(String line)
    {
        String[] words = Strings.util.split(line);
        int[] types = new int[words.length];
        for (int i = 0; i < types.length; i++)
        {
            types[i] = getWordType(words[i]);
        }
        return types;
    }

    private boolean equal(int[] t1, int[] t2)
    {
        if (t1.length != t2.length)
        {
            return false;
        }

        for (int i = 0; i < t1.length; i++)
        {
            if (t1[i] != t2[i])
            {
                return false;
            }
        }

        return true;
    }

    private Block getBlock(int line)
    {
        for (int b = 0; b < blocks.size(); b++)
        {
            Block block = blocks.get(b);
            if (line >= block.start && line < block.end)
            {
                return block;
            }
        }

        return null;
    }

    private String typeToString(int type)
    {
        if (type == WORDTYPE_STRING)
        {
            return ("str ");
        }
        if (type == WORDTYPE_INT)
        {
            return ("int ");
        }
        if (type == WORDTYPE_DOUBLE)
        {
            return ("double ");
        }

        return null;
    }

    private String typesToString(int[] types)
    {
        StringBuilder strb = new StringBuilder();
        for (int i = 0; i < types.length; i++)
        {
            strb.append(typeToString(types[i]));
        }
        return strb.toString();
    }

    private void checkAvailable(int[] types, int word)
    {
        if (!(word < types.length))
        {
            throw (new IllegalArgumentException("Trying to get word with index " + word + " from a line which has only " + types.length + " words of type: " + typesToString(types)));
        }
    }

    private void checkRequest(int line, int word, int expectedType)
    {
        Block b = getBlock(line);
        checkAvailable(b.types, word);
        if (b.types[word] < expectedType)
        {
            throw (new IllegalArgumentException("Trying to read type " + typeToString(expectedType) + " in line " + line + ", word " + word + ", but found wrong type " + typeToString(b.types[word])));
        }
        readLine(line);
    }

    private void checkLineArrayRequest(int line, int expectedType)
    {
        Block b = getBlock(line);
        for (int word = 0; word < b.types.length; word++)
        {
            if (b.types[word] < expectedType)
            {
                throw (new IllegalArgumentException("Trying to read type " + typeToString(expectedType) + " in line " + line + ", word " + word + ", but found wrong type " + typeToString(b.types[word])));
            }
        }
        readLine(line);
    }

    private void readLine(int line)
    {
        if (line != currentLineNumber)
        {
            currentLineNumber = line;
            currentWords = Strings.util.split(super.getLine(line));
        }
    }

    public int getInt(int line, int word)
    {
        checkRequest(line, word, WORDTYPE_INT);
        return Strings.util.toInt(currentWords[word]);
    }

    public double getDouble(int line, int word)
    {
        checkRequest(line, word, WORDTYPE_DOUBLE);
        return Strings.util.toDouble(currentWords[word]);
    }

    public String getWord(int line, int word)
    {
        checkRequest(line, word, WORDTYPE_STRING);
        return currentWords[word];
    }

    public int[] getIntRow(int line)
    {
        checkLineArrayRequest(line, WORDTYPE_INT);
        return Strings.util.toIntArray(currentWords);
    }

    public double[] getDoubleRow(int line)
    {
        checkLineArrayRequest(line, WORDTYPE_DOUBLE);
        return Strings.util.toDoubleArray(currentWords);
    }

    private int countElementsInColumn(int column, int expectedType)
    {
        // count elements
        int size = 0;
        for (Block b : blocks)
        {
            if (b.types.length > column)
            {
                if (b.types[column] >= expectedType)
                {
                    size += b.length;
                }
            }
        }
        return size;
    }

    /**
     * Returns all available integers in the column with the given index.
     *
     * @param column
     * @return
     */
    public int[] getIntColumn(int column)
    {
        int[] res = new int[countElementsInColumn(column, WORDTYPE_INT)];
        int i = 0;
        for (Block b : blocks)
        {
            if (b.types.length > column)
            {
                if (b.types[column] >= WORDTYPE_INT)
                {
                    for (int j = 0; j < b.length; j++)
                    {
                        res[i++] = getInt(b.start + j, column);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Returns all available integers in the column with the given index.
     *
     * @param column
     * @return
     */
    public double[] getDoubleColumn(int column)
    {
        double[] res = new double[countElementsInColumn(column, WORDTYPE_DOUBLE)];
        int i = 0;
        for (Block b : blocks)
        {
            if (b.types.length > column)
            {
                if (b.types[column] >= WORDTYPE_DOUBLE)
                {
                    for (int j = 0; j < b.length; j++)
                    {
                        res[i++] = getDouble(b.start + j, column);
                    }
                }
            }
        }
        return res;
    }

    public String[] getColumn(int column)
    {
        String[] res = new String[countElementsInColumn(column, WORDTYPE_STRING)];
        int i = 0;
        for (Block b : blocks)
        {
            if (b.types.length > column)
            {
                for (int j = 0; j < b.length; j++)
                {
                    res[i++] = getWord(b.start + j, column);
                }
            }
        }
        return res;
    }

    private Block generalizeBlock(Block b, int expectedType)
    {
        Block res = b.copy();
        for (int i = 0; i < res.types.length; i++)
        {
            res.types[i] = Math.min(res.types[i], expectedType);
        }
        return res;
    }

    private List<Block> mergeConsistentBlocks(int expectedType)
    {
        List<Block> res = new ArrayList();
        Block currentBlock = generalizeBlock(blocks.get(0), expectedType);
        
        res.add(currentBlock);
        for (int i = 1; i < blocks.size(); i++)
        {
            Block candidate = generalizeBlock(blocks.get(i), expectedType);
            if (IntsPrimitive.util.equal(currentBlock.types, candidate.types))
            {
                currentBlock.end += candidate.length;
                currentBlock.length += candidate.length;
            }
            else
            {
                currentBlock = candidate;
                res.add(candidate);
            }
        }

        return res;
    }

    private Block getLargestBlock(int expectedType)
    {
        Block largest = null;
        int size = 0;

        List<Block> mergedBlocks = mergeConsistentBlocks(expectedType);
        for (Block b : mergedBlocks)
        {
            int nwords = 0;
            for (int i = 0; i < b.types.length; i++)
            {
                if (b.types[i] >= expectedType)
                {
                    nwords++;
                }
            }
            int currentSize = nwords * b.length;

            if (currentSize > size)
            {
                size = currentSize;
                largest = b;
            }
        }

        return largest;
    }

    private int[] consistentColumns(Block b, int expectedType)
    {
        int[] res = new int[b.types.length];
        int n = 0;
        for (int i = 0; i < b.types.length; i++)
        {
            if (b.types[i] >= expectedType)
            {
                res[i] = i;
                n++;
            }
        }
        return IntsPrimitive.util.subarray(res, 0, n);
    }

    private boolean areBlocksConsistent(Block b1, Block b2, int expectedType)
    {
        int[] c1 = consistentColumns(b1, expectedType);
        int[] c2 = consistentColumns(b2, expectedType);
        return IntsPrimitive.util.equal(c1, c2);
    }

    private int[] selectTypes(int[] types, int expectedType)
    {
        int n = 0;
        for (int i = 0; i < types.length; i++)
        {
            if (types[i] >= expectedType)
            {
                n++;
            }
        }
        int[] res = new int[n];
        int j = 0;
        for (int i = 0; i < types.length; i++)
        {
            if (types[i] >= expectedType)
            {
                res[j++] = i;
            }
        }
        return res;
    }

    public int[] getLargestNumberBlockDimensions()
    {
        return getLargestDoubleBlockDimensions();
    }

    public int[] getLargestDoubleBlockDimensions()
    {
        Block b = getLargestBlock(WORDTYPE_DOUBLE);
        if (b == null)
        {
            int[] res =
            {
                0, 0
            };
            return res;
        }
        else
        {
            int[] res =
            {
                b.length, selectTypes(b.types, WORDTYPE_DOUBLE).length
            };
            return res;
        }
    }

    public int[] getLargestIntBlockDimensions()
    {
        Block b = getLargestBlock(WORDTYPE_INT);
        if (b == null)
        {
            int[] res =
            {
                0, 0
            };
            return res;
        }
        else
        {
            int[] res =
            {
                b.length, selectTypes(b.types, WORDTYPE_INT).length
            };
            return res;
        }
    }

    public int[][] getLargestIntBlock()
    {
        Block b = getLargestBlock(WORDTYPE_INT);
        if (b == null)
        {
            return null;
        }
        int[] indexes = selectTypes(b.types, WORDTYPE_INT);
        int[][] res = new int[b.length][indexes.length];
        for (int i = 0; i < res.length; i++)
        {
            for (int j = 0; j < indexes.length; j++)
            {
                res[i][j] = getInt(i + b.start, indexes[j]);
            }
        }
        return res;
    }

    public double[][] getLargestDoubleBlock()
    {
        Block b = getLargestBlock(WORDTYPE_DOUBLE);
        if (b == null)
        {
            return null;
        }
        int[] indexes = selectTypes(b.types, WORDTYPE_DOUBLE);
        double[][] res = new double[b.length][indexes.length];
        for (int i = 0; i < res.length; i++)
        {
            for (int j = 0; j < indexes.length; j++)
            {
                res[i][j] = getDouble(i + b.start, indexes[j]);
            }
        }
        return res;
    }

    public String[][] getLargestBlock()
    {
        Block b = getLargestBlock(WORDTYPE_STRING);
        String[][] res = new String[b.length][b.types.length];
        for (int i = 0; i < res.length; i++)
        {
            for (int j = 0; j < res[i].length; j++)
            {
                res[i][j] = getWord(i + b.start, j);
            }
        }
        return res;
    }

    class Block
    {

        int[] types;
        int start;
        int end;
        int length;

        public Block(int[] _types, int _start)
        {
            types = _types;
            start = _start;
        }

        public void setLastLine(int lastLine)
        {
            end = lastLine + 1;
            length = lastLine - start + 1;
        }

        public Block copy()
        {
            Block b = new Block(IntsPrimitive.util.copy(types), start);
            b.end = end;
            b.length = length;
            return b;
        }

        @Override
        public String toString()
        {
            return ("block " + start + "-" + end + ". types: " + IntsPrimitive.util.toString(types, ",") + "\n");
        }
    }
}
