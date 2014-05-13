/*
 * Copyright (c) 2009-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mikera.matrixx.algo.decompose.chol.impl;


import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.chol.IChol;

/**
 *
 * <p>
 * This is an abstract class for a Cholesky decomposition.  It provides the solvers, but the actual
 * decompsoition is provided in other classes.
 * </p>
 * <p>
 * A Cholesky Decomposition is a special decomposition for positive-definite symmetric matrices
 * that is more efficient than other general purposes decomposition. It refactors matrices
 * using one of the two following equations:<br>
 * <br>
 * L*L<sup>T</sup>=A<br>
 * R<sup>T</sup>*R=A<br>
 * <br>
 * where L is a lower triangular matrix and R is an upper traingular matrix.<br>
 * </p>
 *
 * @see CholeskyDecompositionInner
 * @see org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionBlock
 * @see org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionLDL
 *
 * @author Peter Abeles
 */
public abstract class CholCommon implements IChol {

    // it can decompose a matrix up to this width
    protected int maxWidth=-1;

    // width and height of the matrix
    protected int n;

    // the decomposed matrix
    protected Matrix T;
    protected double[] t;

    // tempoary variable used by various functions
    protected double vv[];

    /**
     * Creates a CholeksyDecomposition capable of decomposing a matrix that is
     * n by n, where n is the width.
     */
    public CholCommon() {
    }

    public void setExpectedMaxSize( int numRows , int numCols ) {
        if( numRows != numCols ) {
            throw new IllegalArgumentException("Can only decompose square matrices");
        }

        this.maxWidth = numCols;

        this.vv = new double[maxWidth];
    }

    /**
     * <p>
     * Performs Choleksy decomposition on the provided matrix.
     * </p>
     *
     * <p>
     * If the matrix is not positive definite then this function will return
     * false since it can't complete its computations.  Not all errors will be
     * found.  This is an efficient way to check for positive definiteness.
     * </p>
     * @param mat A symmetric positive definite matrix with n <= widthMax.
     * @return True if it was able to finish the decomposition.
     */
    public IChol decompose( AMatrix mat ) {
        if( mat.rowCount() > maxWidth ) {
            setExpectedMaxSize(mat.rowCount(),mat.columnCount());
        } else if( mat.rowCount() != mat.columnCount() ) {
            throw new IllegalArgumentException("Must be a square matrix.");
        }

        n = mat.rowCount();

        T = mat.toMatrix();
        t = T.data;

        return decomposeLower();
    }

    /**
     * Performs an lower triangular decomposition.
     */
    protected abstract IChol decomposeLower();

    /**
     * Returns the lower triangular matrix from the decomposition.
     *
     * @return A lower triangular matrix.
     */
    public AMatrix getL() {
    	return T;
    }

    /**
     * Returns the upper triangular matrix from the decomposition.
     *
     * @return An upper triangular matrix.
     */
    public AMatrix getU() {
        return T.getTranspose();
    }

    public double[] _getVV() {
        return vv;
    }
}