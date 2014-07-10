/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import static stallone.api.API.*;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.coordinates.AbstractCoordinateTransform;
import stallone.coordinates.MinimalRMSDistance3D;
import stallone.coordinates.PCA;
import stallone.coordinates.TICA;

/**
 *
 * @author noe
 */
public class CoordinateFactory
{
    public ICoordinateTransform transform_minrmsd(IDoubleArray Xref)
    {
        class Transform_MinRMSD extends AbstractCoordinateTransform
        {
            IDoubleArray Xref;
            MinimalRMSDistance3D minrmsd;
            public Transform_MinRMSD(IDoubleArray _Xref)
            {
                super(1);
                Xref = _Xref;
                minrmsd = new MinimalRMSDistance3D(Xref.rows());
            }
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                out.set(0,minrmsd.distance(Xref, in));
            }
        }
        return new Transform_MinRMSD(Xref);
    }

    public ICoordinateTransform transform_distances(int[] set1)
    {
        class Transform_Dist extends AbstractCoordinateTransform
        {
            int[] set1;
            public Transform_Dist(int[] _set1)
            {
                super((_set1.length * (_set1.length-1)) / 2);
                set1 = _set1;
            }
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.distances(in, set1, out);
            }
        }
        return new Transform_Dist(set1);
    }

    public ICoordinateTransform transform_distances(int[] set1, int[] set2)
    {
        class Transform_Dist extends AbstractCoordinateTransform
        {
            int[] set1;
            int[] set2;
            public Transform_Dist(int[] _set1, int[] _set2)
            {
                super(_set1.length, _set2.length);
                set1 = _set1;
                set2 = _set2;
            }
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.distanceMatrix(in, set1, set2, out);
            }
        }
        return new Transform_Dist(set1,set2);
    }
    
    public ICoordinateTransform transform_distances(int[][] set1)
    {
        class Transform_Dist extends AbstractCoordinateTransform
        {
            int[][] set1;
            public Transform_Dist(int[][] _set1)
            {
                super((_set1.length * (_set1.length-1)) / 2);
                set1 = _set1;
            }
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.distances(in, set1, out);
            }
        }
        return new Transform_Dist(set1);
    }

    public ICoordinateTransform transform_distances(int[][] set1, int[][] set2)
    {
        class Transform_Dist extends AbstractCoordinateTransform
        {
            int[][] set1;
            int[][] set2;
            public Transform_Dist(int[][] _set1, int[][] _set2)
            {
                super(_set1.length, _set2.length);
                set1 = _set1;
                set2 = _set2;
            }
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.distanceMatrix(in, set1, set2, out);
            }
        }
        return new Transform_Dist(set1,set2);
    }

    public ICoordinateTransform transform_angles(int[][] selection)
    {
        class Transform_Ang extends AbstractCoordinateTransform
        {
            int[][] selection;
            public Transform_Ang(int[][] _selection)
            {
                super(_selection.length);
                selection = _selection;
            }
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.angles(in, selection, out);
            }
        }
        return new Transform_Ang(selection);
    }

    public ICoordinateTransform transform_dihedrals(int[][] selection)
    {
        // same method, just different indexing.
        return transform_angles(selection);
    }

    public ICoordinateTransform transform_selection(int[] selection)
    {
        class Transform_Sel extends AbstractCoordinateTransform
        {
            int[] selection;
            public Transform_Sel(int[] _selection)
            {
                super(_selection.length,3);
                selection = _selection;
            }
            @Override
            public void transform(IDoubleArray in, IDoubleArray out)
            {
                coor.select(in, selection, out);
            }
        }
        return new Transform_Sel(selection);
    }
    
    public IPCA pca(IDataInput datainput)
    {
        return new PCA(datainput);
    }

    public IPCA pca(IDataSequence X)
    {
        IDataInput input = dataNew.dataInput(X);
        return new PCA(input);
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
