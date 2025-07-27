package com.cirmuller.maidaddition.entity.navigation;

import net.minecraft.core.Vec3i;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;

public class ManhattanAdmissibleHeuristic<P extends Vec3i> implements AStarAdmissibleHeuristic<P> {
    @Override
    public double getCostEstimate(P sourceVertex, P targetVertex) {
        return sourceVertex.distManhattan(targetVertex);
    }
}
