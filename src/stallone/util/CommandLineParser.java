/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author noe
 */
public class CommandLineParser
{

    private Arguments args;
    private HashMap<String, Command> commands = new HashMap();
    private ArrayList<String[]> atLeastOneOfCommands = new ArrayList();
    private ArrayList<String[]> atMostOneOfCommands = new ArrayList();

    public void addStringArrayCommand(String command, boolean mandatory)
    {
        addStringArrayCommand(command, mandatory, null, null);
    }

    public void addStringArrayCommand(String command, boolean mandatory, String[] defaultValue, String[] options)
    {
        commands.put(command, new StringArrayCommand(command, mandatory, defaultValue, options));
    }

    public void addIntArrayCommand(String command, boolean mandatory)
    {
        addIntArrayCommand(command, mandatory, null, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public void addIntArrayCommand(String command, boolean mandatory, int[] defaultValue, int minValue, int maxValue)
    {
        commands.put(command, new IntArrayCommand(command, mandatory, defaultValue, minValue, maxValue));
    }

    public void addDoubleArrayArgument(String command, boolean mandatory)
    {
        addDoubleArrayCommand(command, mandatory, null, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public void addDoubleArrayCommand(String command, boolean mandatory, double[] defaultValue, double minValue, double maxValue)
    {
        if (!commands.containsKey(command))
        {
            throw new IllegalArgumentException("Trying to add argument to command " + command + " which does not exist yet");
        }
        commands.put(command, new DoubleArrayCommand(command, mandatory, defaultValue, minValue, maxValue));
    }

    /**
     * Adds a command which may have zero, one, or multiple arguments of any
     * type
     *
     * @param command
     * @param options null, if no options should be considered
     */
    public void addCommand(String command, boolean _mandatory)
    {
        commands.put(command, new HeterogeneousCommand(command, _mandatory));
    }

    public void addStringCommand(String command, boolean _mandatory, String defaultValue, String[] options)
    {
        commands.put(command, new HeterogeneousCommand(command, _mandatory));
        addStringArgument(command, _mandatory, defaultValue, options);
    }

    public void addStringCommand(String command, boolean _mandatory)
    {
        commands.put(command, new HeterogeneousCommand(command, _mandatory));
        addStringArgument(command, _mandatory, null, null);
    }

    public void addIntCommand(String command, boolean _mandatory, int defaultValue, int minValue, int maxValue)
    {
        commands.put(command, new HeterogeneousCommand(command, _mandatory));
        addIntArgument(command, _mandatory, defaultValue, minValue, maxValue);
    }

    public void addIntCommand(String command, boolean _mandatory)
    {
        commands.put(command, new HeterogeneousCommand(command, _mandatory));
        addIntArgument(command, _mandatory);
    }

    public void addDoubleCommand(String command, boolean _mandatory, double defaultValue, double minValue, double maxValue)
    {
        commands.put(command, new HeterogeneousCommand(command, _mandatory));
        addDoubleArgument(command, _mandatory, defaultValue, minValue, maxValue);
    }

    public void addDoubleCommand(String command, boolean _mandatory)
    {
        commands.put(command, new HeterogeneousCommand(command, _mandatory));
        addDoubleArgument(command, _mandatory);
    }

    public void addStringArgument(String command, boolean mandatory)
    {
        addStringArgument(command, mandatory, null, null);
    }

    public void addStringArgument(String command, boolean mandatory, String defaultValue, String[] options)
    {
        if (!commands.containsKey(command))
        {
            throw new IllegalArgumentException("Trying to add argument to command " + command + " which does not exist yet");
        }
        Command cmd = commands.get(command);
        if (!(cmd instanceof HeterogeneousCommand))
        {
            throw new IllegalArgumentException("Trying to add string argument to command " + command + " which does not support another string argument");
        }
        HeterogeneousCommand cmdh = (HeterogeneousCommand) cmd;
        int pos = cmdh.arguments.size();
        cmdh.addArgument(new StringArgument(command, pos, mandatory, defaultValue, options));
    }

    public void addIntArgument(String command, boolean mandatory)
    {
        addIntArgument(command, mandatory, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public void addIntArgument(String command, boolean mandatory, int defaultValue, int minValue, int maxValue)
    {
        if (!commands.containsKey(command))
        {
            throw new IllegalArgumentException("Trying to add argument to command " + command + " which does not exist yet");
        }
        Command cmd = commands.get(command);
        if (!(cmd instanceof HeterogeneousCommand))
        {
            throw new IllegalArgumentException("Trying to add string argument to command " + command + " which does not support another string argument");
        }
        HeterogeneousCommand cmdh = (HeterogeneousCommand) cmd;
        int pos = cmdh.arguments.size();
        cmdh.addArgument(new IntArgument(command, pos, mandatory, defaultValue, minValue, maxValue));
    }

    public void addDoubleArgument(String command, boolean mandatory)
    {
        addDoubleArgument(command, mandatory, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public void addDoubleArgument(String command, boolean mandatory, double defaultValue, double minValue, double maxValue)
    {
        if (!commands.containsKey(command))
        {
            throw new IllegalArgumentException("Trying to add argument to command " + command + " which does not exist yet");
        }
        Command cmd = commands.get(command);
        if (!(cmd instanceof HeterogeneousCommand))
        {
            throw new IllegalArgumentException("Trying to add string argument to command " + command + " which does not support another string argument");
        }
        HeterogeneousCommand cmdh = (HeterogeneousCommand) cmd;
        int pos = cmdh.arguments.size();
        cmdh.addArgument(new DoubleArgument(command, pos, mandatory, defaultValue, minValue, maxValue));
    }

    /*
     * Dependencies between commands
     */
    /**
     * Only one of the following commands is permitted at a time
     *
     * @param exclusive
     */
    public void requireAtMostOneOf(String... exclusives)
    {
        for (String cmd : exclusives)
        {
            if (!commands.containsKey(cmd))
            {
                throw new IllegalArgumentException("Trying to set the command " + cmd + " exclusive. This command has not been defined yet");
            }
        }
        atMostOneOfCommands.add(exclusives);
    }

    /**
     * Only one of the following commands is permitted at a time
     *
     * @param exclusive
     */
    public void requireAtLeastOneOf(String... cmds)
    {
        for (String cmd : cmds)
        {
            if (!commands.containsKey(cmd))
            {
                throw new IllegalArgumentException("Trying to set the command " + cmd + " exclusive. This command has not been defined yet");
            }
        }
        atLeastOneOfCommands.add(cmds);
    }

    public void requireExactlyOneOf(String... cmds)
    {
        requireAtMostOneOf(cmds);
        requireAtLeastOneOf(cmds);
    }
    
    
    private boolean checkIfAllCommandsAreExpected()
    {
        // check if unexpected commands are available
        for (String cmd : args.getCommands())
        {
            boolean found = false;
            for (Command expectedArg : commands.values())
            {
                if (expectedArg.command.equals(cmd))
                {
                    found = true;
                    break;
                }
                else if (expectedArg.command.equalsIgnoreCase(cmd))
                {
                    System.out.println("Found command " + cmd + " but expected " + expectedArg.command + ". Please check case.");
                    return false;
                }
            }
            if (!found)
            {
                System.out.println("Unexpected command " + cmd);
                return false;
            }
        }
        return true;
    }

    private boolean checkCommandDependencies()
    {
        for (String[] group : atMostOneOfCommands)
        {
            String found = null;
            for (String cmd : group)
            {
                if (args.hasCommand(cmd))
                {
                    if (found == null)
                    {
                        found = cmd;
                    }
                    else
                    {
                        System.out.println("Found both commands " + found + " and " + cmd + " which are mutually exclusive.");
                        return false;
                    }
                }
            }
        }

        for (String[] group : atLeastOneOfCommands)
        {
            boolean found = false;
            for (String cmd : group)
            {
                if (args.hasCommand(cmd))
                {
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                System.out.println("Found none of the commands " + StringTools.toString(group) + ". At least one is required");
                return false;
            }
        }

        return true;
    }

    public boolean parse(String[] commandline)
    {
        this.args = new Arguments(commandline);

        if (!checkIfAllCommandsAreExpected())
        {
            return false;
        }

        if (!checkCommandDependencies())
        {
            return false;
        }

        for (String cmdstr : args.getCommands())
        {
            Command cmd = commands.get(cmdstr);
            cmd.setArguments(args);
            cmd.parse();
        }

        return true;
    }

    private Command retrieve(String cmd)
    {
        if (!commands.containsKey(cmd))
        {
            throw (new IllegalArgumentException("Trying to read command " + cmd + " which has not been defined"));
        }
        Command c = commands.get(cmd);

        return c;
    }

    private ExpectedArgument retrieve(String cmd, int iArg)
    {
        if (!commands.containsKey(cmd))
        {
            throw (new IllegalArgumentException("Trying to read command " + cmd + " which has not been defined"));
        }
        Command c = commands.get(cmd);
        if (!(c instanceof HeterogeneousCommand))
        {
            throw (new IllegalArgumentException("Trying to read single argument " + iArg + " of a command " + cmd + " which supports only an array. Get the array as a whole"));
        }
        HeterogeneousCommand ch = (HeterogeneousCommand) c;
        if (ch.arguments.size() <= iArg)
        {
            throw (new IllegalArgumentException("Trying to read argument " + iArg + " of command " + cmd + " which has not been defined"));
        }

        return ch.arguments.get(iArg);
    }

    public boolean hasCommand(String cmd)
    {
        return (args.hasCommand(cmd));
    }

    public String getString(String cmd, int iArg)
    {
        ExpectedArgument ea = retrieve(cmd, iArg);
        if (!(ea instanceof StringArgument))
        {
            throw (new IllegalArgumentException("Trying to read argument " + iArg + " of command " + cmd + " as string argument, but it is of a different type"));
        }
        return ((StringArgument) ea).value;
    }

    public String[] getStringArray(String cmd)
    {
        Command c = retrieve(cmd);
        if (!(c instanceof StringArrayCommand))
        {
            throw (new IllegalArgumentException("Trying to read argument of command " + cmd + " as int array argument, but it is of a different type"));
        }
        return ((StringArrayCommand) c).value;
    }

    public int getInt(String cmd, int iArg)
    {
        ExpectedArgument ea = retrieve(cmd, iArg);
        if (!(ea instanceof IntArgument))
        {
            throw (new IllegalArgumentException("Trying to read argument " + iArg + " of command " + cmd + " as int argument, but it is of a different type"));
        }
        return ((IntArgument) ea).value;
    }

    public int[] getIntArray(String cmd)
    {
        Command c = retrieve(cmd);
        if (!(c instanceof IntArrayCommand))
        {
            throw (new IllegalArgumentException("Trying to read argument of command " + cmd + " as int array argument, but it is of a different type"));
        }
        return ((IntArrayCommand) c).value;
    }

    public double getDouble(String cmd, int iArg)
    {
        ExpectedArgument ea = retrieve(cmd, iArg);
        if (!(ea instanceof DoubleArgument))
        {
            throw (new IllegalArgumentException("Trying to read argument " + iArg + " of command " + cmd + " as double argument, but it is of a different type"));
        }
        return ((DoubleArgument) ea).value;
    }

    public double[] getDoubleArray(String cmd)
    {
        Command c = retrieve(cmd);
        if (!(c instanceof DoubleArrayCommand))
        {
            throw (new IllegalArgumentException("Trying to read argument of command " + cmd + " as double array argument, but it is of a different type"));
        }
        return ((DoubleArrayCommand) c).value;
    }

    /**
     * returns first Argument to given cmd
     */
    public String getString(String cmd)
    {
        return (getString(cmd, 0));
    }

    public int getInt(String cmd)
    {
        return (getInt(cmd, 0));
    }

    public double getDouble(String cmd)
    {
        return (getDouble(cmd, 0));
    }
}

class Command
{

    Arguments args;
    int nargsFound;
    String command;
    boolean mandatory = false;
    int minNumberOfArgs = 0;
    int maxNumberOfArgs = -1;

    public Command(String cmd, boolean _mandatory)
    {
        command = cmd;
        mandatory = _mandatory;
    }

    public void setNumberOfExpectedArgs(int min, int max)
    {
        this.minNumberOfArgs = min;
        this.maxNumberOfArgs = max;
    }

    public void setArguments(Arguments _args)
    {
        this.args = _args;
        this.nargsFound = 0;
        if (_args.hasCommand(command))
            this.nargsFound = _args.getNArguments(command);
    }

/*    public void setNumberOfFoundArgs(int _nargs)
    {
        System.out.println("setting number of found args in "+command+": "+_nargs);
        this.nargsFound = _nargs;
    }
*/
    public boolean parse()
    {
        // check if command is present
        if (args.hasCommand(command))
        {
            // check if the minimum number of arguments are right
            if (nargsFound < minNumberOfArgs)
            {
                System.out.println("Command -" + command + " has " + nargsFound
                        + " arguments. Expecting at least " + minNumberOfArgs + " arguments.");
                return false;
            }

            // check if the maximum number of arguments is right
            if (maxNumberOfArgs >= 0 && nargsFound > maxNumberOfArgs)
            {
                System.out.println("Command -" + command + " has " + nargsFound
                        + " arguments. Expecting at most " + maxNumberOfArgs + " arguments.");
                return false;
            }

            return true;
        }
        else
        {
            if (mandatory)
            {
                System.out.println("Expected command -" + command + " is not present");
                return false;
            }
            else
            {
                return true;
            }
        }
    }
}

class HeterogeneousCommand extends Command
{

    List<ExpectedArgument> arguments = new ArrayList();

    public HeterogeneousCommand(String cmd, boolean _mandatory)
    {
        super(cmd, _mandatory);
    }

    public void addArgument(ExpectedArgument arg)
    {
        if (arg.mandatory)
        {
            addMandatoryArgument();
        }
        else
        {
            addOptionalArgument();
        }
        arguments.add(arg);
    }

    private void addMandatoryArgument()
    {
        if (maxNumberOfArgs > minNumberOfArgs)
        {
            throw new IllegalArgumentException("Trying to add a mandatory argument after an optional argument. This is forbidden.");
        }

        if (maxNumberOfArgs == -1)
        {
            minNumberOfArgs = 1;
            maxNumberOfArgs = 1;
        }
        else
        {
            minNumberOfArgs++;
            maxNumberOfArgs++;
        }
    }

    private void addOptionalArgument()
    {
        if (maxNumberOfArgs == -1)
        {
            minNumberOfArgs = 0;
            maxNumberOfArgs = 1;
        }
        else
        {
            maxNumberOfArgs++;
        }
    }

    @Override
    public boolean parse()
    {
        if (!super.parse())
        {
            return false;
        }

        // check if command is present
        if (args.hasCommand(command))
        {
            // parse all arguments
            for (ExpectedArgument ea : arguments)
            {
                if (!ea.parse(args))
                {
                    return false;
                }
            }
        }

        return true;
    }
}

class StringArrayCommand extends Command
{

    String[] options;
    String[] value;

    public StringArrayCommand(String cmd, boolean _mandatory, String[] defaultValue, String[] _options)
    {
        super(cmd, _mandatory);
        value = defaultValue;
        options = _options;
    }

    @Override
    public boolean parse()
    {
        if (!super.parse())
        {
            return false;
        }

        value = args.getArguments(command);

        // check options
        if (options != null)
        {
            for (int i = 0; i < value.length; i++)
            {
                if (!StringTools.contains(options, value[i]))
                {
                    System.out.println("Command -" + command + " has unexpected argument: " + value[i]
                            + ". Expected arguments are: { " + StringTools.toString(options, ", ") + " }");
                    return false;
                }
            }
        }

        return true;
    }
}

class IntArrayCommand extends Command
{

    int minValue, maxValue;
    int[] value;

    public IntArrayCommand(String cmd, boolean _mandatory, int[] _defaultValue, int _minValue, int _maxValue)
    {
        super(cmd, _mandatory);
        value = _defaultValue;
        minValue = _minValue;
        maxValue = _maxValue;

        if (_defaultValue != null)
        {
            for (int dv : _defaultValue)
            {
                if (!(minValue <= dv && dv <= maxValue))
                {
                    throw new IllegalArgumentException("Default value " + _defaultValue + " of integer argument " + cmd + " is out of range [" + _minValue + "," + _maxValue + "]");
                }
            }
        }
    }

    @Override
    public boolean parse()
    {
        String[] argsFound = args.getArguments(command);
        value = new int[argsFound.length];

        for (int i = 0; i < argsFound.length; i++)
        {
            if (!StringTools.isInt(argsFound[i]))
            {
                System.out.println("Command -" + command + " has unexpected argument: " + argsFound[i]
                        + ". Expect an integer value");
            }

            value[i] = StringTools.toInt(argsFound[i]);
            if (!(minValue <= value[i] && value[i] <= maxValue))
            {
                System.out.println("Command -" + command + " has unexpected argument value: " + value
                        + ". Expecting an integer in the range [" + minValue + "," + maxValue + "]");
            }
        }
        return true;
    }

    public int[] getValue()
    {
        return value;
    }
}

class DoubleArrayCommand extends Command
{

    double minValue, maxValue;
    double[] value;

    public DoubleArrayCommand(String cmd, boolean _mandatory, double[] _defaultValue, double _minValue, double _maxValue)
    {
        super(cmd, _mandatory);
        value = _defaultValue;
        minValue = _minValue;
        maxValue = _maxValue;

        if (_defaultValue != null)
        {
            for (double dv : _defaultValue)
            {
                if (!(minValue <= dv && dv <= maxValue))
                {
                    throw new IllegalArgumentException("Default value " + _defaultValue + " of double argument " + cmd + " is out of range [" + _minValue + "," + _maxValue + "]");
                }
            }
        }
    }

    @Override
    public boolean parse()
    {
        String[] argsFound = args.getArguments(command);
        value = new double[argsFound.length];

        for (int i = 0; i < argsFound.length; i++)
        {
            if (!StringTools.isDouble(argsFound[i]))
            {
                System.out.println("Command -" + command + " has unexpected argument: " + argsFound[i]
                        + ". Expect a double value");
            }

            value[i] = StringTools.toDouble(argsFound[i]);
            if (!(minValue <= value[i] && value[i] <= maxValue))
            {
                System.out.println("Command -" + command + " has unexpected argument value: " + value
                        + ". Expecting a double in the range [" + minValue + "," + maxValue + "]");
            }
        }
        return true;
    }

    public double[] getValue()
    {
        return value;
    }
}

abstract class ExpectedArgument
{

    String command;
    int position;
    boolean mandatory = true;

    public ExpectedArgument(String _command, int _position, boolean _mandatory)
    {
        command = _command;
        position = _position;
        mandatory = _mandatory;
    }

    public boolean parse(Arguments args)
    {
        // if the argument is not available, but expected, return false
        if (args.getNArguments(command) <= position)
        {
            if (mandatory)
                return false;
            else
                return true;
        }
        
        return parseWhenAvailable(args);
    }
    
    protected abstract boolean parseWhenAvailable(Arguments args);
            
}

class StringArgument extends ExpectedArgument
{

    String[] options;
    String value;

    public StringArgument(String cmd, int pos, boolean _mandatory, String defaultValue, String[] _options)
    {
        super(cmd, pos, _mandatory);
        value = defaultValue;
        options = _options;
    }

    @Override
    public boolean parseWhenAvailable(Arguments args)
    {
        
        // check options
        if (options != null)
        {
            value = args.getArgument(command, position);
            if (!StringTools.contains(options, value))
            {
                System.out.println("Command -" + command + " has unexpected argument: " + value
                        + ". Expected arguments are: { " + StringTools.toString(options, ", ") + " }");
            }

            return true;
        }
        else
        {
            value = args.getArgument(command, position);
            return true;
        }
    }
}

class IntArgument extends ExpectedArgument
{

    int minValue, maxValue;
    int value;

    public IntArgument(String cmd, int pos, boolean _mandatory, int _defaultValue, int _minValue, int _maxValue)
    {
        super(cmd, pos, _mandatory);
        value = _defaultValue;
        minValue = _minValue;
        maxValue = _maxValue;

        if (!mandatory && !(minValue <= value && value <= maxValue))
        {
            throw new IllegalArgumentException("Default value " + _defaultValue + " of integer argument " + cmd + " " + pos + " is out of range [" + _minValue + "," + _maxValue + "]");
        }
    }

    @Override
    public boolean parseWhenAvailable(Arguments args)
    {
        String argFound = args.getArgument(command, position);

        if (!StringTools.isInt(argFound))
        {
            System.out.println("Command -" + command + " has unexpected argument: " + argFound
                    + ". Expect an integer value");
        }

        value = StringTools.toInt(argFound);
        if (!(minValue <= value && value <= maxValue))
        {
            System.out.println("Command -" + command + " has unexpected argument value: " + value
                    + ". Expecting an integer in the range [" + minValue + "," + maxValue + "]");
        }

        return true;
    }

    public int getValue()
    {
        return value;
    }
}

class DoubleArgument extends ExpectedArgument
{

    double minValue, maxValue;
    double value;

    public DoubleArgument(String cmd, int pos, boolean _mandatory, double _defaultValue, double _minValue, double _maxValue)
    {
        super(cmd, pos, _mandatory);
        value = _defaultValue;
        minValue = _minValue;
        maxValue = _maxValue;

        if (!mandatory && !(minValue <= value && value <= maxValue))
        {
            throw new IllegalArgumentException("Default value " + _defaultValue + " of double argument " + cmd + " " + pos + " is out of range [" + _minValue + "," + _maxValue + "]");
        }
    }

    @Override
    public boolean parseWhenAvailable(Arguments args)
    {
        String argFound = args.getArgument(command, position);

        if (!StringTools.isDouble(argFound))
        {
            System.out.println("Command -" + command + " has unexpected argument: " + argFound
                    + ". Expect a double value");
        }

        value = StringTools.toDouble(argFound);
        if (!(minValue <= value && value <= maxValue))
        {
            System.out.println("Command -" + command + " has unexpected argument value: " + value
                    + ". Expecting a double in the range [" + minValue + "," + maxValue + "]");
        }

        return true;
    }

    public double getValue()
    {
        return value;
    }
}
