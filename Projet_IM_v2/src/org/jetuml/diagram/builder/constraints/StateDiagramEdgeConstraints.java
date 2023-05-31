/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/

package org.jetuml.diagram.builder.constraints;

import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;

/**
 * Methods to create edge addition constraints that only apply to
 * state diagrams. CSOFF:
 */
public final class StateDiagramEdgeConstraints
{
	private StateDiagramEdgeConstraints() {}
	
	/*
	 * No edges are allowed into an Initial Node
	 */
	public static Constraint noEdgeToInitialNode()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, DiagramRenderer pRenderer)->
		{
			return pEnd.getClass() != InitialStateNode.class;
		};
	}
	
	/*
	 * The only edge allowed out of a FinalNode is a NoteEdge
	 */
	public static Constraint noEdgeFromFinalNode()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, DiagramRenderer pRenderer)->
		{
			return !(pStart.getClass() == FinalStateNode.class && pEdge.getClass() != NoteEdge.class );
		};
	}
}