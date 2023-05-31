/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package org.jetuml.persistence;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetuml.JetUML;
import org.jetuml.application.Version;
import org.jetuml.persistence.json.JsonArray;
import org.jetuml.persistence.json.JsonException;
import org.jetuml.persistence.json.JsonObject;
import org.jetuml.persistence.json.JsonParser;

/**
 * Utility class to migrate a pre-3.0 saved diagram to post 3.0.
 * 
 * Rules: 
 * * A PackageNode with both content and children will lose its content 
 * * A PackageNode with only content (no children) will be transformed into a PackageDescriptionNode
 * * DependencyEdge elements that have the same start and end node are removed 
 * * The startLabel and endLabel property of DependencyEdges are removed 
 * * A directionality property is added to DependencyEdge elements 
 * * Dependency edges between two same nodes, but in both directions, are replaced with a single 
 *       bidirectional edge whose label is the concatenation of the two original ones.
 * * The interface stereotype is removed from the name of interface nodes.
 * * All labels of GeneralizationEdges will be dropped
 * * AssociationEdges with a "Start" directionality will be flipped
 * * The types of associations are renamed
 */
public final class VersionMigrator
{
	private boolean aMigrated;

	/**
	 * Creates a new version migrator. Can be reused.
	 */
	public VersionMigrator()
	{}

	/**
	 * Currently for testing.
	 * 
	 * @param pArgs
	 *            Not used
	 * @throws IOException
	 *             If there's a problem.
	 */
	public static void main(String[] pArgs) throws IOException
	{
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream("test.class.jet"), StandardCharsets.UTF_8)))
		{
			new VersionMigrator().migrate(JsonParser.parse(in.readLine()));
		}
		catch( JsonException e )
		{
			throw new DeserializationException("Cannot decode the file", e);
		}
	}
	
	/**
	 * @param pDiagram
	 *            The loaded diagram to migrate
	 * @return A migrated Diagram object.
	 */
	public VersionedDiagram migrate(JsonObject pDiagram)
	{
		Version version = Version.parse(pDiagram.getString("version"));

		if( version.compatibleWith(JetUML.VERSION)) // We don't need to migrate the diagram, it's compatible
		{
			return new VersionedDiagram(JsonDecoder.decode(pDiagram), version, false);
		}
		
		aMigrated = false;

		// JSONObject to JSONObject conversions
		convertPackageNodeToPackageDescriptionNode(pDiagram);
		removeSelfDependencies(pDiagram);
		addDirectionalityPropertyToDependencyEdges(pDiagram);
		replaceDualDependenciesWithBidirectionalEdge(pDiagram);
		removeInterfaceStereotype(pDiagram);
		flipInversedAssociations(pDiagram);
		renameAssociationDirectionality(pDiagram);

		return new VersionedDiagram(JsonDecoder.decode(pDiagram), version, aMigrated);
	}

	private void convertPackageNodeToPackageDescriptionNode(JsonObject pDiagram)
	{
		JsonArray nodes = pDiagram.getJsonArray("nodes");
		for( int i = 0; i < nodes.size(); i++ )
		{
			JsonObject object = nodes.getJsonObject(i);
			if( object.getString("type").equals("PackageNode") && !object.hasProperty("children") && object.hasProperty("contents") )
			{
				object.put("type", "PackageDescriptionNode");
				aMigrated = true;
			}
		}
	}
	
	private void removeInterfaceStereotype(JsonObject pDiagram)
	{
		JsonArray nodes = pDiagram.getJsonArray("nodes");
		for( int i = 0; i < nodes.size(); i++ )
		{
			JsonObject object = nodes.getJsonObject(i);
			if( object.getString("type").equals("InterfaceNode") )
			{
				if( object.getString("name").contains("\u00ABinterface\u00BB"))
				{
					object.put("name", object.getString("name").replace("\u00ABinterface\u00BB", "").trim());
					aMigrated = true;
				}
			}
		}
	}

	private void removeSelfDependencies(JsonObject pDiagram)
	{
		JsonArray edges = pDiagram.getJsonArray("edges");
		List<JsonObject> newEdges = new ArrayList<>();
		for( int i = 0; i < edges.size(); i++ )
		{
			JsonObject object = edges.getJsonObject(i);
			if( object.getString("type").equals("DependencyEdge") && object.getInt("start") == object.getInt("end") )
			{
				aMigrated = true; // We don't add the dependency, essentially removing it.
			}
			else
			{
				newEdges.add(object);
			}
		}
		pDiagram.put("edges", new JsonArray(newEdges));
	}

	private void addDirectionalityPropertyToDependencyEdges(JsonObject pDiagram)
	{
		JsonArray edges = pDiagram.getJsonArray("edges");
		for( int i = 0; i < edges.size(); i++ )
		{
			JsonObject object = edges.getJsonObject(i);
			if( object.getString("type").equals("DependencyEdge") )
			{
				object.put("directionality", "Unidirectional");
				aMigrated = true;
			}
		}
	}
	
	/*
	 * Replace associations with a "Start" directionality with
	 * a directional edge in the reverse direction. 
	 */
	private void flipInversedAssociations(JsonObject pDiagram)
	{
		JsonArray edges = pDiagram.getJsonArray("edges");
		for( int i = 0; i < edges.size(); i++ )
		{
			JsonObject object = edges.getJsonObject(i);
			if( object.getString("type").equals("AssociationEdge") && object.getString("directionality").equals("Start"))
			{
				object.put("directionality", "End");
				int start = object.getInt("start");
				int end = object.getInt("end");
				object.put("start", end);
				object.put("end", start);
				aMigrated = true;
			}
		}
	}
	
	private void renameAssociationDirectionality(JsonObject pDiagram)
	{
		JsonArray edges = pDiagram.getJsonArray("edges");
		for( int i = 0; i < edges.size(); i++ )
		{
			JsonObject object = edges.getJsonObject(i);
			if( object.getString("type").equals("AssociationEdge"))
			{
				if( object.get("directionality").equals("None"))
				{
					object.put("directionality", "Unspecified");
				}
				else if( object.get("directionality").equals("End"))
				{
					object.put("directionality", "Unidirectional");
				}
				else if( object.get("directionality").equals("Both"))
				{
					object.put("directionality", "Bidirectional");
				}
				aMigrated = true;
			}
		}
	}

	private void replaceDualDependenciesWithBidirectionalEdge(JsonObject pDiagram)
	{
		Map<Set<Integer>, JsonObject> links = new HashMap<>();
		List<JsonObject> newEdges = new ArrayList<>();
		
		JsonArray edges = pDiagram.getJsonArray("edges");
		for( int i = 0; i < edges.size(); i++ )
		{
			JsonObject object = edges.getJsonObject(i);
			newEdges.add(object);
			if( object.getString("type").equals("DependencyEdge") ) 
			{
//				Set<Integer> key = Set.of(object.getInt("start"), object.getInt("end"));
				Set<Integer> key = new HashSet<>(Arrays.asList(object.getInt("start"), object.getInt("end")));

				
				if( links.containsKey(key))
				{
					newEdges.remove(object);
					links.get(key).put("directionality", "Bidirectional");
					links.get(key).put("middleLabel", links.get(key).get("middleLabel") + " + " + object.get("middleLabel"));
					aMigrated = true;
				}
				else
				{
//					links.put(Set.of(object.getInt("start"), object.getInt("end")), object);
					links.put(new HashSet<>(Arrays.asList(object.getInt("start"), object.getInt("end"))), object);

				}
			}
		}
		pDiagram.put("edges", new JsonArray(newEdges));
	}
}
