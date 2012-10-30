/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maths.highlevelapi.functions.DOGMAFunctions.DOGMAArithmetic.ctranspose;

import com.opengamma.maths.highlevelapi.datatypes.primitive.OGDiagonalMatrix;
import com.opengamma.maths.lowlevelapi.functions.checkers.Catchers;

/**
 * Conjugate Transposes an OGDiagonalArray
 * TODO: At some point consider COW or at least state propagated permutations for things like this?!
 */
public final class CtransposeOGDiagonalMatrix implements CtransposeAbstract<OGDiagonalMatrix> {
  private static CtransposeOGDiagonalMatrix s_instance = new CtransposeOGDiagonalMatrix();

  public static CtransposeOGDiagonalMatrix getInstance() {
    return s_instance;
  }

  private CtransposeOGDiagonalMatrix() {
  }

  @Override
  public OGDiagonalMatrix ctranspose(OGDiagonalMatrix array1) {
    Catchers.catchNullFromArgList(array1, 1);
    return new OGDiagonalMatrix(array1.getData(), array1.getNumberOfColumns(), array1.getNumberOfRows());
  }

}