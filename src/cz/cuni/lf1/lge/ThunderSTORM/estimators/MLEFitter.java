package cz.cuni.lf1.lge.ThunderSTORM.estimators;

import cz.cuni.lf1.lge.ThunderSTORM.estimators.PSF.PSFInstance;
import cz.cuni.lf1.lge.ThunderSTORM.estimators.PSF.PSFModel;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;

/**
 *
 * @author Josef Borkovec <josef.borkovec[at]lf1.cuni.cz>
 */
public class MLEFitter implements OneLocationFitter {

  PSFModel psfModel;
  final static int MAX_ITERATIONS = 10000;

  public MLEFitter(PSFModel psfModel) {
    this.psfModel = psfModel;
  }

  @Override
  public PSFInstance fit(SubImage subimage) {

    SimplexOptimizer optimizer = new SimplexOptimizer(10e-10, 10e-10);
    PointValuePair pv;

    pv = optimizer.optimize(
            MaxEval.unlimited(),
            new MaxIter(MAX_ITERATIONS),
            new ObjectiveFunction(psfModel.getLikelihoodFunction(subimage.xgrid, subimage.ygrid, subimage.values)),
            new InitialGuess(psfModel.transformParametersInverse(psfModel.getInitialParams(subimage))),
            GoalType.MINIMIZE,
            new NelderMeadSimplex(psfModel.getInitialSimplex()));
    double[] point = pv.getPointRef();
    //point[0] = point[0] * Math.sqrt(2 * Math.PI) * point[3] * point[3];
    return psfModel.newInstanceFromParams(psfModel.transformParameters(point));
  }
}