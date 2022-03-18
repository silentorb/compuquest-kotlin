package compuquest.population

import godot.*
import godot.global.GD

const val baseTexture = "_base_"

object Textures {
	const val cliffWall = "cliff-wall"
	const val cold = "cold"
	const val dirt = "dirt"
	const val grassGreen = "grass-green"
	const val grayBricks = "gray-bricks"
	const val lightBlueBricks = "light-blue-bricks"
}

typealias MaterialMap = MutableMap<String, Material>

fun newBaseMaterial(materials: MaterialMap): Material {
	val newBase = GD.load<Material>("res://assets/materials/texture.tres")!!
	materials[baseTexture] = newBase
	return newBase
}

fun getBaseTextureMaterial(materials: MaterialMap): Material =
	materials[baseTexture] ?: newBaseMaterial(materials)

fun newTileMaterial(materials: MaterialMap, texture: String): Material {
	val base = getBaseTextureMaterial(materials)
	val material = base.duplicate() as SpatialMaterial
	val imageTexture = GD.load<Texture>("res://assets/images/tiles/$texture.png")
	material.albedoTexture = imageTexture
	materials[texture] = material
	return material
}

fun getMaterial(materials: MaterialMap, texture: String): Material =
	materials[texture] ?: newTileMaterial(materials, texture)
