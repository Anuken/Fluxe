package net.pixelstatic.fluxe.meshes;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class MarchingCubes{
	// Marching cubes stuff

	// Tables for marcing cubes
	int[] edgeTable = {0x0, 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c, 0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03,
			0xe09, 0xf00, 0x190, 0x99, 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c, 0x99c, 0x895, 0xb9f, 0xa96, 0xd9a,
			0xc93, 0xf99, 0xe90, 0x230, 0x339, 0x33, 0x13a, 0x636, 0x73f, 0x435, 0x53c, 0xa3c, 0xb35, 0x83f, 0x936,
			0xe3a, 0xf33, 0xc39, 0xd30, 0x3a0, 0x2a9, 0x1a3, 0xaa, 0x7a6, 0x6af, 0x5a5, 0x4ac, 0xbac, 0xaa5, 0x9af,
			0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0, 0x460, 0x569, 0x663, 0x76a, 0x66, 0x16f, 0x265, 0x36c, 0xc6c, 0xd65,
			0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60, 0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff, 0x3f5, 0x2fc, 0xdfc,
			0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0, 0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55, 0x15c,
			0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950, 0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5,
			0xcc, 0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0, 0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf,
			0xec5, 0xfcc, 0xcc, 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0, 0x950, 0x859, 0xb53, 0xa5a, 0xd56,
			0xc5f, 0xf55, 0xe5c, 0x15c, 0x55, 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650, 0xaf0, 0xbf9, 0x8f3, 0x9fa,
			0xef6, 0xfff, 0xcf5, 0xdfc, 0x2fc, 0x3f5, 0xff, 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0, 0xb60, 0xa69, 0x963,
			0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c, 0x36c, 0x265, 0x16f, 0x66, 0x76a, 0x663, 0x569, 0x460, 0xca0, 0xda9,
			0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac, 0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa, 0x1a3, 0x2a9, 0x3a0, 0xd30,
			0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c, 0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33, 0x339, 0x230,
			0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c, 0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99,
			0x190, 0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c, 0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203,
			0x109, 0x0};
	int[][] triTable = {{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1},
			{3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1},
			{3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
			{3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1},
			{9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1},
			{9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
			{2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1},
			{8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1},
			{9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
			{4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1},
			{3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1},
			{1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1},
			{4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1},
			{4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
			{9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
			{5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1},
			{2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1},
			{9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
			{0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
			{2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1},
			{10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1},
			{4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1},
			{5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1},
			{5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1},
			{9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1},
			{0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1},
			{1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1},
			{10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1},
			{8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1},
			{2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1},
			{7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1},
			{9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1},
			{2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1},
			{11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1},
			{9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1},
			{5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1},
			{11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1},
			{11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
			{1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1},
			{9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1},
			{5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1},
			{2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
			{0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
			{5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1},
			{6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1},
			{3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1},
			{6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1},
			{5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1},
			{1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
			{10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1},
			{6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1}, {8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1},
			{7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1},
			{3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
			{5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1},
			{0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1},
			{9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1},
			{8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1},
			{5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1}, {0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1},
			{6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1},
			{10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1},
			{10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1},
			{8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1},
			{1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1},
			{3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1},
			{0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1},
			{10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1},
			{3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1},
			{6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1},
			{9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1}, {8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1},
			{3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1},
			{6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1},
			{0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1},
			{10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1},
			{10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1}, {2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1},
			{7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1},
			{7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1},
			{2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1}, {1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1},
			{11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1},
			{8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1},
			{0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1},
			{7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
			{10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
			{2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
			{6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1},
			{7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1},
			{2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1},
			{1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1},
			{10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1},
			{10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1},
			{0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1},
			{7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1},
			{6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1},
			{8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1},
			{9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1},
			{6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1},
			{4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1},
			{10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1},
			{8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1},
			{0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1},
			{1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1},
			{8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1},
			{10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1},
			{4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1},
			{10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
			{5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
			{11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1},
			{9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
			{6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1},
			{7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1},
			{3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1},
			{7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
			{9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1}, {3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1},
			{6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1}, {9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1},
			{1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1}, {4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1},
			{7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1},
			{6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1},
			{3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1},
			{0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1},
			{6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1},
			{0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1}, {11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1},
			{6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1},
			{5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1},
			{9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1}, {1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1},
			{1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1}, {10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1},
			{0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1},
			{5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1},
			{10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1},
			{11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1},
			{9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1}, {7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1},
			{2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1},
			{8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1},
			{9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1}, {9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1},
			{1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1},
			{9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1},
			{9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1},
			{5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1},
			{0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1},
			{10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1},
			{2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1},
			{0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1},
			{0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1},
			{9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1},
			{5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1},
			{3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1}, {5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1},
			{8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1},
			{0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1},
			{9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1},
			{0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1},
			{1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1},
			{3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1},
			{4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1},
			{9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1},
			{11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1},
			{11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1},
			{2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1}, {9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1},
			{3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1},
			{1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1},
			{4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1},
			{4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1},
			{0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1},
			{3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1},
			{3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1},
			{0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1},
			{9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1},
			{1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};

	class vec3{
		float x, y, z;

		public vec3(){
		}

		public vec3(float x, float y, float z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	vec3 VertexInterp2(float isolevel, vec3 p1, vec3 p2, float valp1, float valp2){

		if(Math.abs(isolevel - valp1) < 0.00001) return (p1);
		if(Math.abs(isolevel - valp2) < 0.00001) return (p2);
		if(Math.abs(valp1 - valp2) < 0.00001) return (p1);

		float mu = (isolevel - valp1) / (valp2 - valp1);

		vec3 p = new vec3();
		p.x = p1.x + mu * (p2.x - p1.x);
		p.y = p1.y + mu * (p2.y - p1.y);
		p.z = p1.z + mu * (p2.z - p1.z);

		return (p);
	}

	int width = 50, height = 50, depth = 50;

	public void march(Mesh mesh, int[][][] grid){
		width = grid.length;
		height = grid[0].length;
		depth = grid[0][0].length;

		printf("Create grid\n");

		printf("Create vectors and octree\n");

		FloatArray vertices = new FloatArray();
		ShortArray indices = new ShortArray();

		OctreeNode octtree = new OctreeNode(0, null, 0.0, 0.0, 0.0, width, height, depth);

		printf("March cubes\n");

		for(int k = 0;k < depth - 1;k ++){
			for(int j = 0;j < height - 1;j ++){
				for(int i = 0;i < width - 1;i ++){
					vec3[] vertlist = new vec3[12];

					float isolevel = 0.5f;
					int cubeindex = 0;

					if(grid[i][j + 1][k] == 0) cubeindex |= 1;
					if(grid[i][j][k] == 0) cubeindex |= 2;

					if(grid[i + 1][j][k] == 0) cubeindex |= 4;
					if(grid[i + 1][j + 1][k] == 0) cubeindex |= 8;

					if(grid[i][j + 1][k + 1] == 0) cubeindex |= 16;
					if(grid[i][j][k + 1] == 0) cubeindex |= 32;
					if(grid[i + 1][j][k + 1] == 0) cubeindex |= 64;
					if(grid[i + 1][j + 1][k + 1] == 0) cubeindex |= 128;

					if(edgeTable[cubeindex] == 0) continue;

					if((edgeTable[cubeindex] & 1) != 0) vertlist[0] = VertexInterp2(isolevel, new vec3(i, j + 1, k), new vec3(i, j, k), grid[i][j + 1][k], grid[i][j][k]);
					if((edgeTable[cubeindex] & 2) != 0) vertlist[1] = VertexInterp2(isolevel, new vec3(i, j, k), new vec3(i + 1, j, k), grid[i][j][k], grid[i + 1][j][k]);
					if((edgeTable[cubeindex] & 4) != 0) vertlist[2] = VertexInterp2(isolevel, new vec3(i + 1, j, k), new vec3(i + 1, j + 1, k), grid[i + 1][j][k], grid[i + 1][j + 1][k]);
					if((edgeTable[cubeindex] & 8) != 0) vertlist[3] = VertexInterp2(isolevel, new vec3(i + 1, j + 1, k), new vec3(i, j + 1, k), grid[i + 1][j + 1][k], grid[i][j + 1][k]);
					if((edgeTable[cubeindex] & 16) != 0) vertlist[4] = VertexInterp2(isolevel, new vec3(i, j + 1, k + 1), new vec3(i, j, k + 1), grid[i][j + 1][k + 1], grid[i][j][k + 1]);
					if((edgeTable[cubeindex] & 32) != 0) vertlist[5] = VertexInterp2(isolevel, new vec3(i, j, k + 1), new vec3(i + 1, j, k + 1), grid[i][j][k + 1], grid[i + 1][j][k + 1]);
					if((edgeTable[cubeindex] & 64) != 0) vertlist[6] = VertexInterp2(isolevel, new vec3(i + 1, j, k + 1), new vec3(i + 1, j + 1, k + 1), grid[i + 1][j][k + 1], grid[i + 1][j + 1][k + 1]);
					if((edgeTable[cubeindex] & 128) != 0) vertlist[7] = VertexInterp2(isolevel, new vec3(i + 1, j + 1, k + 1), new vec3(i, j + 1, k + 1), grid[i + 1][j + 1][k + 1], grid[i][j + 1][k + 1]);
					if((edgeTable[cubeindex] & 256) != 0) vertlist[8] = VertexInterp2(isolevel, new vec3(i, j + 1, k), new vec3(i, j + 1, k + 1), grid[i][j + 1][k], grid[i][j + 1][k + 1]);
					if((edgeTable[cubeindex] & 512) != 0) vertlist[9] = VertexInterp2(isolevel, new vec3(i, j, k), new vec3(i, j, k + 1), grid[i][j][k], grid[i][j][k + 1]);
					if((edgeTable[cubeindex] & 1024) != 0) vertlist[10] = VertexInterp2(isolevel, new vec3(i + 1, j, k), new vec3(i + 1, j, k + 1), grid[i + 1][j][k], grid[i + 1][j][k + 1]);
					if((edgeTable[cubeindex] & 2048) != 0) vertlist[11] = VertexInterp2(isolevel, new vec3(i + 1, j + 1, k), new vec3(i + 1, j + 1, k + 1), grid[i + 1][j + 1][k], grid[i + 1][j + 1][k + 1]);

					/* Create the triangle */
					for(int id = 0;triTable[cubeindex][id] != -1;id += 3){

						int key_out1 = octtree.insert(vertlist[triTable[cubeindex][id]].x, vertlist[triTable[cubeindex][id]].y, vertlist[triTable[cubeindex][id]].z, 0, vertices);
						int key_out2 = octtree.insert(vertlist[triTable[cubeindex][id + 1]].x, vertlist[triTable[cubeindex][id + 1]].y, vertlist[triTable[cubeindex][id + 1]].z, 0, vertices);
						int key_out3 = octtree.insert(vertlist[triTable[cubeindex][id + 2]].x, vertlist[triTable[cubeindex][id + 2]].y, vertlist[triTable[cubeindex][id + 2]].z, 0, vertices);

						indices.add((short)key_out1);
						indices.add((short)key_out2);
						indices.add((short)key_out3);

					}
				}
			}
		}

		printf("Done marching cubes\n");

		printf("Mesh stats:\n");
		printf("  %dx%dx%d = %d voxels\n", width, height, depth, depth * height * depth);
		printf("  %d vertices\n", (int)vertices.size);
		printf("  %d triangles\n", (int)indices.size / 3);

		short[] newindices = indices.toArray();

		float[] normals = addNormals(vertices.toArray(), newindices);//createNormals(vertices.toArray(), newindices);

		mesh.setVertices(normals);
		mesh.setIndices(newindices);

		for(int i = 0;i < normals.length;i ++){
			//newindices[i]*=2;
			System.out.println(normals[i]);

			//	System.out.println();
		}

		// short[] indices

		/*
		printf("save file as .obj\n"); fflush(stdout);
		FILE *fp = fopen("mesh.obj", "w");
		fprintf(fp, "####\n");
		fprintf(fp, "#\n");
		fprintf(fp, "# OBJ File\n");
		fprintf(fp, "#\n");
		fprintf(fp, "####\n");
		fprintf(fp, "# Object mesh.obj\n");
		fprintf(fp, "#\n");
		fprintf(fp, "# Vertices: %d\n", vertices.size()/3);
		fprintf(fp, "# Faces: %d\n", indices.size()/3);
		fprintf(fp, "#\n");
		fprintf(fp, "####\n");
		
		for (int i = 0; i < vertices.size()/3; i++) {
		    fprintf(fp, "v %f %f %f\n", vertices[3*i+0], vertices[3*i+1], vertices[3*i+2]);
		}
		fprintf(fp, "# %d vertices, 0 vertices normals\n\n", vertices.size()/3);
		for (int i = 0; i < indices.size()/3; i++) {
		    fprintf(fp, "f %d %d %d\n", indices[3*i+0]+1, indices[3*i+1]+1, indices[3*i+2]+1);
		}
		
		fprintf(fp, "# %d faces, 0 coords texture\n", indices.size()/3);
		fprintf(fp, "\n");
		fprintf(fp, "# End of File\n");
		fclose(fp);
		*/

	}

	float[] addNormals(float[] vertices, short[] indices){
		float[] normals = new float[2 * vertices.length];
		
		//temporary vector
		Vector3 v = new Vector3();
		
		//add normals
		for(int i = 0;i < indices.length / 3;i ++){
			int a = indices[i * 3 + 0] * 3;
			int b = indices[i * 3 + 1] * 3;
			int c = indices[i * 3 + 2] * 3;

			float x1 = vertices[a + 0];
			float y1 = vertices[a + 1];
			float z1 = vertices[a + 2];

			float x2 = vertices[b + 0];
			float y2 = vertices[b + 1];
			float z2 = vertices[b + 2];

			float x3 = vertices[c + 0];
			float y3 = vertices[c + 1];
			float z3 = vertices[c + 2];
			
			//v.set(x1, y1, z1);
			v.set(x3 - x1, y3 - y1, z3 - z1).crs(x2 - x1, y2 - y1, z2 - z1).scl(-1).nor();

			normals[a*2 + 0 + 3] += v.x;
			normals[a*2 + 1 + 3] += v.y;
			normals[a*2 + 2 + 3] += v.z;

			normals[b*2 + 0 + 3] += v.x;
			normals[b*2 + 1 + 3] += v.y;
			normals[b*2 + 2 + 3] += v.z;

			normals[c*2 + 0 + 3] += v.x;
			normals[c*2 + 1 + 3] += v.y;
			normals[c*2 + 2 + 3] += v.z;
			
		//	if(i < 3)
		//		System.out.printf("a: %d, b: %d, c: %d -[]- x1: %f, y1: %f, z1: %f -[]- x2: %f, y2: %f, z2: %f -[]- x3: %f, y3: %f, z3: %f\n", a, b, c, x1, y1, z1, x2, y2, z2, x3, y3, z3);
		}
		
		//normalize final normals
		for(int i = 0;i < normals.length / 6;i ++){
			int o = i*6;
			
			float nx = normals[o + 0 + 3];
			float ny = normals[o + 1 + 3];
			float nz = normals[o + 2 + 3];
			
			
			v.set(nx, ny, nz).nor();
		//	System.out.println(v);
			
			normals[o + 0 + 3] = v.x;
			normals[o + 1 + 3] = v.y;
			normals[o + 2 + 3] = v.z;
		}

		/*
		for(int i = 0; i < vertices.length/9-1; i ++){
			int vindex = i*3;
			
			int a = indices[vindex]*3;
			int b = indices[vindex+1]*3;
			int c = indices[vindex+2]*3;
			
			if(i < 6){
				System.out.printf("a: %d, b: %d, c: %d\n", a, b, c);
			}
			
			float x1 = vertices[a+0];
			float y1 = vertices[a+1];
			float z1 = vertices[a+2];
			
			float x2 = vertices[b+0];
			float y2 = vertices[b+1];
			float z2 = vertices[b+2];
			
			float x3 = vertices[c+0];
			float y3 = vertices[c+1];
			float z3 = vertices[c+2];
			
			v1.set(x3 - x1, y3 - y1, z3 - z1).crs(x2 - x1, y2 - y1, z2 - z1).nor();
			
			
			int noff = i*3*2;
			if(i < 6)
			System.out.println("noff: " + noff);
			
			normals[a*2+3] += v1.x;
			normals[a*2+4] += v1.y;
			normals[a*2+5] += v1.z;
			
			normals[b*2+3] += v1.x;
			normals[b*2+4] += v1.y;
			normals[b*2+5] += v1.z;
			
			normals[c*2+3] += v1.x;
			normals[c*2+4] += v1.y;
			normals[c*2+5] += v1.z;
			
		}
		*/
		
		//add vertices
		for(int i = 0;i < (vertices.length) * 2;i ++){
			if(i % 6 < 3){
				normals[i] = vertices[i % 3 + (i / 6) * 3];
			}
		}

		return normals;
	}

	float[] createNormals(float[] vertices, short[] indices){
		int nVertices = vertices.length, nTriangles = nVertices / 3;

		float[] normals = new float[3 * nVertices];
		for(int i = 0;i < 3 * nVertices;i ++)
			normals[i] = 0.0f;

		int numnans = 0;

		Vector3 v1 = new Vector3();
		Vector3 v2 = new Vector3();

		// add (normalized) normals from adjacent triangles to each vertex
		for(int i = 0;i < nTriangles;i ++){
			int id1 = indices[3 * i + 0];
			int id2 = indices[3 * i + 1];
			int id3 = indices[3 * i + 2];
			float x1 = vertices[3 * id1 + 0];
			float y1 = vertices[3 * id1 + 1];
			float z1 = vertices[3 * id1 + 2];
			float x2 = vertices[3 * id2 + 0];
			float y2 = vertices[3 * id2 + 1];
			float z2 = vertices[3 * id2 + 2];
			float x3 = vertices[3 * id3 + 0];
			float y3 = vertices[3 * id3 + 1];
			float z3 = vertices[3 * id3 + 2];

			float dx1 = x2 - x1;
			float dx2 = x3 - x1;
			float dy1 = y2 - y1;
			float dy2 = y3 - y1;
			float dz1 = z2 - z1;
			float dz2 = z3 - z1;

			Vector3 normal = v1.set(dx1, dy1, dz1).crs(dx2, dy2, dz2).nor();

			float len = normal.dot(normal);
			if((len) == Float.NaN)
				numnans ++;
			// printf("%d %d %d, (%f %f %f) (%f %f %f) (%f %f %f)\n", id1, id2, id3, x1,y1,z1, x2, y2, z2, x3, y3, z3); fflush(stdout);
			else{
				normals[3 * id1 + 0] += normal.x;
				normals[3 * id1 + 1] += normal.y;
				normals[3 * id1 + 2] += normal.z;

				normals[3 * id2 + 0] += normal.x;
				normals[3 * id2 + 1] += normal.y;
				normals[3 * id2 + 2] += normal.z;

				normals[3 * id3 + 0] += normal.x;
				normals[3 * id3 + 1] += normal.y;
				normals[3 * id3 + 2] += normal.z;
			}

		}
		printf("num NaNs = %d\n", numnans);

		// normalize final normal

		return normals;
	}

	class OctreeNode{
		OctreeNode nwu, neu, swu, seu;
		OctreeNode nwd, ned, swd, sed;
		OctreeNode up;
		int key;
		boolean leaf;
		int level;
		double x, y, z;
		double dx, dy, dz;

		OctreeNode(int level, OctreeNode up, double x, double y, double z, double dx, double dy, double dz){
			this.level = level;
			this.up = up;
			nwu = null;
			neu = null;
			swu = null;
			seu = null;
			nwd = null;
			ned = null;
			swd = null;
			sed = null;
			key = -1;
			this.x = x;
			this.y = y;
			this.z = z;
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}

		void split(FloatArray vertices){
			nwu = new OctreeNode(level + 1, this, x, y + dy / 2, z + dz / 2, dx / 2, dy / 2, dz / 2);
			neu = new OctreeNode(level + 1, this, x + dx / 2, y + dy / 2, z + dz / 2, dx / 2, dy / 2, dz / 2);
			swu = new OctreeNode(level + 1, this, x, y, z + dz / 2, dx / 2, dy / 2, dz / 2);
			seu = new OctreeNode(level + 1, this, x + dx / 2, y, z + dz / 2, dx / 2, dy / 2, dz / 2);

			nwd = new OctreeNode(level + 1, this, x, y + dy / 2, z, dx / 2, dy / 2, dz / 2);
			ned = new OctreeNode(level + 1, this, x + dx / 2, y + dy / 2, z, dx / 2, dy / 2, dz / 2);
			swd = new OctreeNode(level + 1, this, x, y, z, dx / 2, dy / 2, dz / 2);
			sed = new OctreeNode(level + 1, this, x + dx / 2, y, z, dx / 2, dy / 2, dz / 2);

			float vertex_x = vertices.get(3 * key + 0);
			float vertex_y = vertices.get(3 * key + 1);
			float vertex_z = vertices.get(3 * key + 2);

			// insert
			if(vertex_z < z + dz / 2){
				if(vertex_y < y + dy / 2){
					if(vertex_x < x + dx / 2){
						swd.key = key;
					}else{
						sed.key = key;
					}
				}else{
					if(vertex_x < x + dx / 2){
						nwd.key = key;
					}else{
						ned.key = key;
					}
				}
			}else{
				if(vertex_y < y + dy / 2){
					if(vertex_x < x + dx / 2){
						swu.key = key;
					}else{
						seu.key = key;
					}
				}else{
					if(vertex_x < x + dx / 2){
						nwu.key = key;
					}else{
						neu.key = key;
					}
				}
			}
			key = -1;
		}

		int insert(float new_x, float new_y, float new_z, int key_out, FloatArray vertices){
			if(nwu == null){ // Check to see if node is leaf
				if(key == -1){ // Empty leaf, insert new key
					key = vertices.size / 3;
					key_out = key;
					vertices.add(new_x);
					vertices.add(new_y);
					vertices.add(new_z);
					return key_out;
				}else{ // Not empty leaf. Need to split
					   // Check if position corresponding to this key is sufficiently close to the current key's positoin

					float old_x = vertices.get(3 * key + 0);
					float old_y = vertices.get(3 * key + 1);
					float old_z = vertices.get(3 * key + 2);

					float dx = new_x - old_x;
					float dy = new_y - old_y;
					float dz = new_z - old_z;

					if(dx * dx + dy * dy + dz * dz > 1.0e-9){
						split(vertices);
					}else{
						// vertex already in octtree
						key_out = key;
						return key_out;
					}

				}
			}

			// insert key into appropriate leaf
			if(new_z < z + dz / 2){
				if(new_y < y + dy / 2){
					if(new_x < x + dx / 2){
						key_out = swd.insert(new_x, new_y, new_z, key_out, vertices);
					}else{
						key_out = sed.insert(new_x, new_y, new_z, key_out, vertices);
					}
				}else{
					if(new_x < x + dx / 2){
						key_out = nwd.insert(new_x, new_y, new_z, key_out, vertices);
					}else{
						key_out = ned.insert(new_x, new_y, new_z, key_out, vertices);
					}
				}
			}else{
				if(new_y < y + dy / 2){
					if(new_x < x + dx / 2){
						key_out = swu.insert(new_x, new_y, new_z, key_out, vertices);
					}else{
						key_out = seu.insert(new_x, new_y, new_z, key_out, vertices);
					}
				}else{
					if(new_x < x + dx / 2){
						key_out = nwu.insert(new_x, new_y, new_z, key_out, vertices);
					}else{
						key_out = neu.insert(new_x, new_y, new_z, key_out, vertices);
					}
				}
			}
			key = -1;

			return key_out;
		}

	}

	int index(int x, int y, int z){
		return (z) * (width) * (height) + (y) * (width) + (x);
	}

	void printf(String string, Object...args){
		System.out.printf(string, args);
	}
}
