/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import static stallone.api.API.coor;
import stallone.api.API;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.coordinates.AbstractCoordinateTransform;
import stallone.coordinates.MinimalRMSDistance3D;
import stallone.coordinates.PCA;
import stallone.coordinates.TICA;
import stallone.doubles.DenseDoubleArray;
import stallone.doubles.PrimitiveDoubleTools;

/**
 *
 * @author noe
 */
public class CoordinateFactory
{
    public ICoordinateTransform transform_minrmsd(final IDoubleArray Xref)
    {
        return new AbstractCoordinateTransform(1)
        {
            MinimalRMSDistance3D minrmsd = new MinimalRMSDistance3D(Xref.rows());

            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                out.set(0, minrmsd.distance(Xref, in));
            }
        };
    }

    public ICoordinateTransform transform_distances(final int[] set1)
    {
        return new AbstractCoordinateTransform((set1.length * (set1.length-1)) / 2)
        {
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.distances(in, set1, out);
            }
        };
    }

    public ICoordinateTransform transform_distances(final int[] set1, final int[] set2)
    {
        return new AbstractCoordinateTransform(set1.length, set2.length)
        {
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.distanceMatrix(in, set1, set2, out);
            }
        };
    }
    
    public ICoordinateTransform transform_distances(final int[][] set1)
    {
        return new AbstractCoordinateTransform(set1.length * (set1.length-1) / 2)
        {
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.distances(in, set1, out);
            }
        };
    }

    public ICoordinateTransform transform_distances(final int[][] set1, final int[][] set2)
    {
        return new AbstractCoordinateTransform(set1.length, set2.length)
        {
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.distanceMatrix(in, set1, set2, out);
            }
        };
    }

    public ICoordinateTransform transform_angles(final int[][] selection)
    {
        return new AbstractCoordinateTransform(selection.length)
        {
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.angles(in, selection, out);
            }
        };
    }

    public ICoordinateTransform transform_dihedrals(int[][] selection)
    {
        // same method, just different indexing.
        return transform_angles(selection);
    }

    public ICoordinateTransform transform_selection(final int[] selection)
    {
        return new AbstractCoordinateTransform(selection.length, 3)
        {
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.select(in, selection, out);
            }
        };
    }
    
    /**
     * intended to perform the application of a linear operator A with
     * shape (n,m) to a vector with shape (n)
     * @param A matrix operator
     * @return CoordinateTransformation which performs <A, b> = x
     */
    public ICoordinateTransform linear_operator(final IDoubleArray A) {
        int rows = A.rows(), cols = 1;
        return new AbstractCoordinateTransform(rows, cols)
        {
            @Override
            public void transform(final IDoubleArray in, IDoubleArray out)
            {
                IDoubleArray input = in;
                // workaround to handle n x 3 vectors given by trajectory readers like DCD.
                // flatten array, if compatible. Efficient for DenseDoubleArray, since it has linear memory layout.
                if(in.rows() * in.columns() == A.columns())
                {
                    double[] flat = null;
                    if (in instanceof DenseDoubleArray)
                    {
                        flat = ((DenseDoubleArray) in).getArray();
                    }
                    else
                    {
                        flat = PrimitiveDoubleTools.flatten(in.getTable());
                    }
                    input = new DenseDoubleArray(flat);
                }
                // apply operator
                API.alg.product(A, input, out);
            }
        };
    }
    
    public IPCA pca(IDataInput datainput)
    {
        return new PCA(datainput);
    }

    public IPCA pca(IDataSequence X)
    {
        return new PCA(X);
    }

    public ITICA tica(IDataInput input, int lagtime)
    {
        return new TICA(input, lagtime);
    }

    public ITICA tica(IDataSequence X, int lagtime)
    {
        return new TICA(X, lagtime);
    }
    
}
