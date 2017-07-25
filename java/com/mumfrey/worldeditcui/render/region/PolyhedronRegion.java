package com.mumfrey.worldeditcui.render.region;

import java.util.ArrayList;
import java.util.List;

import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.render.ConfiguredColour;
import com.mumfrey.worldeditcui.render.points.PointCube;
import com.mumfrey.worldeditcui.render.shapes.Render3DPolygon;
import com.mumfrey.worldeditcui.util.Vector3;

/**
 * Main controller for a polygon-type region
 * 
 * @author TomyLobo
 * @author Adam Mummery-Smith
 */
public class PolyhedronRegion extends Region
{
	private static final Vector3 HALF = new Vector3(0.5, 0.5, 0.5);
	
	private List<PointCube> vertices = new ArrayList<PointCube>();
	private List<Vector3[]> faces = new ArrayList<Vector3[]>();
	
	private List<Render3DPolygon> faceRenders = new ArrayList<Render3DPolygon>();
	
	public PolyhedronRegion(WorldEditCUI controller)
	{
		super(controller, ConfiguredColour.POLYBOX, ConfiguredColour.POLYPOINT, ConfiguredColour.CUBOIDPOINT1);
	}
	
	@Override
	public void render(Vector3 cameraPos)
	{
		for (PointCube vertex : this.vertices)
		{
			vertex.render(cameraPos);
		}
		
		for (Render3DPolygon face : this.faceRenders)
		{
			face.render(cameraPos);
		}
	}

	@Override
	public void setCuboidPoint(int id, double x, double y, double z)
	{
		final PointCube vertex = new PointCube(x, y, z).setId(id);
		vertex.setColour(id == 0 ? this.colours[2] : this.colours[1]);
		
		if (id < this.vertices.size())
		{
			this.vertices.set(id, vertex);
		}
		else
		{
			for (int i = 0; i < id - this.vertices.size(); i++)
			{
				this.vertices.add(null);
			}
			this.vertices.add(vertex);
		}
	}
	
	@Override
	public void addPolygon(int[] vertexIds)
	{
		final Vector3[] face = new Vector3[vertexIds.length];
		for (int i = 0; i < vertexIds.length; ++i)
		{
			final PointCube vertex = this.vertices.get(vertexIds[i]);
			if (vertex == null)
			{
				// This should never happen
				return;
			}
			
			face[i] = vertex.getPoint().add(HALF);
		}
		this.faces.add(face);
		this.update();
	}
	
	private void update()
	{
		this.faceRenders.clear();
		
		for (Vector3[] face : this.faces)
		{
			this.faceRenders.add(new Render3DPolygon(this.colours[0], face));
		}
	}
	
	@Override
	protected void updateColours()
	{
		for (PointCube vertex : this.vertices)
		{
			vertex.setColour(vertex.getId() == 0 ? this.colours[2] : this.colours[1]);
		}
		
		for (Render3DPolygon face : this.faceRenders)
		{
			face.setColour(this.colours[0]);
		}
	}

	@Override
	public RegionType getType()
	{
		return RegionType.POLYHEDRON;
	}
}
