package silentorb.mythic.haft

fun applyInputProcessor(processor: InputProcessor, value: Float): Float =
	when (processor.type) {
		InputProcessorType.deadzone -> if (value > 0f)
			if (value <= processor.float1) 0f else value
		else
			if (value >= -processor.float1) 0f else value
		InputProcessorType.invert -> -value
		InputProcessorType.scale -> value * processor.float1
	}

fun applyInputProcessors(processors: List<InputProcessor>, value: Float) =
	processors.fold(value) { a, processor -> applyInputProcessor(processor, a) }
