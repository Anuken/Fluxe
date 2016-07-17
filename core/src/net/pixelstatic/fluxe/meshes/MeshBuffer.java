package net.pixelstatic.fluxe.meshes;


public class MeshBuffer{
	/*
	private final VertexInfo vertTmp1 = new VertexInfo();
	private final VertexInfo vertTmp2 = new VertexInfo();
	private final VertexInfo vertTmp3 = new VertexInfo();
	private final VertexInfo vertTmp4 = new VertexInfo();
	private final Color color = new Color(1, 0, 0, 1);
	private final Vector3[] vectors = new Vector3[8];
	private Mesh currentMesh;
	private ShortBuffer indices;
	private FloatBuffer vertices;
	
	{
		for(int i = 0;i < vectors.length;i ++){
			vectors[i] = new Vector3();
		}
	}
	
	public void cube(float x, float y, float z, float size, boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back){

		vectors[0].set(x, y, z);
		vectors[1].set(x, y, z + size);
		vectors[2].set(x + size, y, z + size);
		vectors[3].set(x + size, y, z);

		vectors[4].set(x, y + size, z);
		vectors[5].set(x, y + size, z + size);
		vectors[6].set(x + size, y + size, z + size);
		vectors[7].set(x + size, y + size, z);

		if(top) rect(vectors[4], vectors[5], vectors[6], vectors[7], Normals.up); //top
		if(bottom) rect(vectors[3], vectors[2], vectors[1], vectors[0], Normals.down); //bottom

		if(left) rect(vectors[5], vectors[4], vectors[0], vectors[1], Normals.left); //left
		if(right) rect(vectors[2], vectors[3], vectors[7], vectors[6], Normals.right); //right

		if(front) rect(vectors[6], vectors[5], vectors[1], vectors[2], Normals.front); //front
		if(back) rect(vectors[3], vectors[0], vectors[4], vectors[7], Normals.back); //back
	}
	
	private void rect(Vector3 a, Vector3 b, Vector3 c, Vector3 d, Vector3 normal){
		meshBuilder.rect(vertTmp1.set(a, normal, color, null).setUV(0f, 1f), vertTmp2.set(b, normal, color, null).setUV(1f, 1f), vertTmp3.set(c, normal, color, null).setUV(1f, 0f), vertTmp4.set(d, normal, color, null).setUV(0f, 0f));
	}
	
	private void index (short value1, short value2, short value3, short value4, short value5, short value6) {
		
		indices.put(value1);
		indices.put(value2);
		indices.put(value3);
		indices.put(value4);
		indices.put(value5);
		indices.put(value6);
	}
	
	private void vertex(VertexInfo info){
		
	}
	
	public short vertex (Vector3 pos, Vector3 nor, Color col, Vector2 uv) {
		if (vertices.position() >= Short.MAX_VALUE) throw new GdxRuntimeException("Too many vertices used");

		vertex[posOffset] = pos.x;
		if (posSize > 1) vertex[posOffset + 1] = pos.y;
		if (posSize > 2) vertex[posOffset + 2] = pos.z;

		if (norOffset >= 0) {
			if (nor == null) nor = tmpNormal.set(pos).nor();
			vertex[norOffset] = nor.x;
			vertex[norOffset + 1] = nor.y;
			vertex[norOffset + 2] = nor.z;
		}

		if (colOffset >= 0) {
			if (col == null) col = Color.WHITE;
			vertex[colOffset] = col.r;
			vertex[colOffset + 1] = col.g;
			vertex[colOffset + 2] = col.b;
			if (colSize > 3) vertex[colOffset + 3] = col.a;
		} else if (cpOffset > 0) {
			if (col == null) col = Color.WHITE;
			vertex[cpOffset] = col.toFloatBits(); 
		}

		if (uv != null && uvOffset >= 0) {
			vertex[uvOffset] = uv.x;
			vertex[uvOffset + 1] = uv.y;
		}

		addVertex(vertex, 0);
		return lastIndex;
	}
	
	private ShortBuffer indices(){
		return currentMesh.getIndicesBuffer();
	}
	
	private FloatBuffer vertices(){
		return currentMesh.getVerticesBuffer();
	}
	*/
}
