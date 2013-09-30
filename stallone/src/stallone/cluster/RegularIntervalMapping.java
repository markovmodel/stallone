package stallone.cluster;

import stallone.api.ints.IIndexMap;

/**
 * @author  Martin Senne
 */
public class RegularIntervalMapping implements IIndexMap {

    private int from;
    private int to;
    private int stepwidth;

    private int size;

    public RegularIntervalMapping(int from, int to, int stepwidth) {
        this.from = from;
        this.to = to;
        this.stepwidth = stepwidth;

        this.size = (to - from) / stepwidth;
    }

    @Override
    public int map(int i) {

        if ((0 <= i) && (i < size)) {
            return (from + (i * stepwidth));
        } else {
            throw new RuntimeException("Index i is not 0 <= i < size.");
        }
    }

    @Override
    public int size() {
        return size;
    }

    public static RegularIntervalMapping createInstance(int from, int to, int stepwidth) {
        return new RegularIntervalMapping(from, to, stepwidth);
    }
}
