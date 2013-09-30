package stallone.io;

import java.io.File;
import java.io.FileFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * The implementation of a FileFilter, which uses the special characters (in a ms-dos like manner)
 *
 * <ul>
 *   <li><b>*</b> to represent a string of arbitrary length</li>
 *   <li><b>?</b> to represent one arbitrary character</li>
 *   <li><b>#</b> to represent an integer number consisting of at least one digit.</li>
 * </ul>
 *
 * With the following files in a directory
 * <pre>
 *   a001.dat
 *   a005.dat
 *   a5.dat
 *   a_old.dat
 *   b.dat
 * </pre>
 *
 * example 1:
 *
 * <pre>
 *     a#.dat finds a001.dat
 *                  a005.dat
 *                  a5.dat
 * </pre>
 *
 * example 2:
 *
 * <pre>
 *     a*.dat finds a001.dat
 *                  a005.dat
 *                  a5.dat
 *                  a_old.dat
 * </pre>
 *
 * @author  Martin Senne, Jan-Hendrik Prinz
 */
public class WildcardFilter implements FileFilter
{

    private Pattern compiledSearchPattern;

    public WildcardFilter(String searchPattern)
    {

        StringBuilder c = new StringBuilder("");
        Pattern nonWildcardBlock = Pattern.compile("[^#*?]+");

        int pos = 0;
        Matcher matcher = nonWildcardBlock.matcher(searchPattern);

        // turn the search pattern into a regular expression
        // original character within searchpattern are enclosed into quotation by "\Q" and "\E"
        // 
        // a*b#.abc => \Qa\E.*\Qb\E\\d+.\Qabc\E
        while (pos < searchPattern.length())
        {

            if (searchPattern.charAt(pos) == '#')
            {
                c.append("\\d+"); // # => \d+
                pos++;
            }
            else if (searchPattern.charAt(pos) == '*')
            {
                c.append(".*"); // "*" => ".*";
                pos++;
            }
            else if (searchPattern.charAt(pos) == '?')
            {
                c.append("."); // "?" => ".";
                pos++;
            }
            else if (matcher.find(pos))
            { // not found # or * then it must be a nonWildCardBlock

                String block = matcher.group();
                pos += block.length();
                c.append(Pattern.quote(block)); // quote and append
            }
            else
            {
                throw new RuntimeException("This should never happen.");
            }
        }

        compiledSearchPattern = Pattern.compile(c.toString());
        // System.out.println(compiledSearchPattern);
    }

    @Override
    public boolean accept(File pathname)
    {
        return compiledSearchPattern.matcher(pathname.getName()).matches();
    }

    public static void main(String[] args)
    {
        File homeDir = new File("/home/fischbac");

        for (File f : homeDir.listFiles(new WildcardFilter("*conf?")))
        {
            System.out.println(f.getAbsolutePath());
        }

    }
}
