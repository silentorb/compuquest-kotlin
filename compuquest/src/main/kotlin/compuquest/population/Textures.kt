package compuquest.population

import godot.*
import godot.global.GD

object Textures {
	const val _base_ = "_base_"
	const val cliffWall = "cliff-wall"
	const val grassGreen = "grass-green"
}

typealias MaterialMap = MutableMap<String, Material>

fun newBaseMaterial(materials: MaterialMap): Material {
	val newBase = GD.load<Material>("res://assets/materials/texture.tres")!!
	materials[Textures._base_] = newBase
	return newBase
}

fun getBaseTextureMaterial(materials: MaterialMap): Material =
	materials[Textures._base_] ?: newBaseMaterial(materials)

fun newTileMaterial(materials: MaterialMap, texture: String): Material {
	val base = getBaseTextureMaterial(materials)
	val material = base.duplicate() as SpatialMaterial
	val imageTexture = GD.load<Texture>("res://assets/images/tiles/$texture.png")
	material.albedoTexture = imageTexture
	val k = material.albedoTexture
	materials[texture] = material
	return material
}

fun getMaterial(materials: MaterialMap, texture: String): Material =
	materials[texture] ?: newTileMaterial(materials, texture)
