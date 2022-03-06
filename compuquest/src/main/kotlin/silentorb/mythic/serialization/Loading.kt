package silentorb.mythic.serialization

import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

fun listFilesAndFoldersRecursive(path: Path): List<Path> =
	if (Files.isDirectory(path))
		listOf(path) +
				Files.list(path)
					.use { paths ->
						paths
							.toList()
							.filterIsInstance<Path>()
							.flatMap { child ->
								listFilesAndFoldersRecursive(child)
							}
					}
	else
		listOf(path)
