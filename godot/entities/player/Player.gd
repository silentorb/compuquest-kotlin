extends KinematicBody

###################-VARIABLES-####################

# Camera
export(float) var mouse_sensitivity = 8.0
export(NodePath) var head_path = "Head"
export(NodePath) var cam_path = "Head/Camera"
export(float) var FOV = 80.0
var mouse_axis := Vector2()
onready var head: Spatial = get_node(head_path)
onready var cam: Camera = get_node(cam_path)
# Move
var velocity := Vector3()
var direction := Vector3()
var move_axis := Vector2()
var snap := Vector3()
var sprint_enabled := true
var sprinting := false
# Walk
const FLOOR_MAX_ANGLE: float = deg2rad(46.0)
export(float) var gravity = 30.0
export(float) var walk_speed = 10
export(float) var sprint_speed = 16
export(float) var acceleration = 8
export(float) var deacceleration = 10
export(float, 0.0, 1.0, 0.05) var air_control = 0.3
export(float) var jump_height = 10
var _speed: float
var _is_sprinting_input := false
var _is_jumping_input := false
var isSlowed = false

var isActive = true

##################################################

# Called when the node enters the scene tree
func _ready() -> void:
	Input.set_mouse_mode(Input.MOUSE_MODE_CAPTURED)
	cam.fov = FOV
	_speed = walk_speed


# Called every frame. 'delta' is the elapsed time since the previous frame
func _process(_delta: float) -> void:
	if isActive:
		Input.set_mouse_mode(Input.MOUSE_MODE_CAPTURED)
		move_axis.x = Input.get_action_strength("move_forward") - Input.get_action_strength("move_backward")
		move_axis.y = Input.get_action_strength("move_right") - Input.get_action_strength("move_left")

		if Input.is_action_just_pressed("move_jump"):
			_is_jumping_input = true

		if Input.is_action_pressed("move_sprint"):
			_is_sprinting_input = true
	else:
		Input.set_mouse_mode(Input.MOUSE_MODE_VISIBLE)


# Called every physics tick. 'delta' is constant
func _physics_process(delta: float) -> void:
	if isActive:
		walk(delta)


# Called when there is an input event
func _input(event: InputEvent) -> void:
	if isActive:
		if event is InputEventMouseMotion:
			mouse_axis = event.relative
			camera_rotation()


func walk(delta: float) -> void:
	direction_input()
	
	var isGrounded = is_on_floor()
	if isGrounded:
		snap = -get_floor_normal() - get_floor_velocity() * delta
		
		# Workaround for sliding down after jump on slope
		if velocity.y < 0:
			velocity.y = 0
		
		jump()
	else:
		# Workaround for 'vertical bump' when going off platform
		if snap != Vector3.ZERO && velocity.y != 0:
			velocity.y = 0
		
		snap = Vector3.ZERO
		
		velocity.y -= gravity * delta
	
	#sprint(delta)
	updateSpeed()
	
	accelerate(delta)

	var new_velocity = move_and_slide_with_snap(velocity, snap, Vector3.UP, true, 4, FLOOR_MAX_ANGLE)
#	var height_diff = translation.y - previous_translation.y
#	if !isGrounded and velocity.y < -1 and abs(height_diff) < 0.001:
#		jump()
#		translate(new_velocity * delta)
#	else:
#		velocity = new_velocity
	
	_is_jumping_input = false
	_is_sprinting_input = false


func camera_rotation() -> void:
	if Input.get_mouse_mode() == Input.MOUSE_MODE_CAPTURED and mouse_axis.length() > 0:
		var horizontal: float = -mouse_axis.x * (mouse_sensitivity / 100)
		var vertical: float = -mouse_axis.y * (mouse_sensitivity / 100)
		
		mouse_axis = Vector2()
		
		rotate_y(deg2rad(horizontal))
		head.rotate_x(deg2rad(vertical))
		
		# Clamp mouse rotation
		var temp_rot: Vector3 = head.rotation_degrees
		temp_rot.x = clamp(temp_rot.x, -90, 90)
		head.rotation_degrees = temp_rot


func direction_input() -> void:
	direction = Vector3()
	var aim: Basis = get_global_transform().basis
	if move_axis.x >= 0.5:
		direction -= aim.z
	if move_axis.x <= -0.5:
		direction += aim.z
	if move_axis.y <= -0.5:
		direction -= aim.x
	if move_axis.y >= 0.5:
		direction += aim.x
	direction.y = 0
	direction = direction.normalized()


func accelerate(delta: float) -> void:
	# Where would the player go
	var _temp_vel: Vector3 = velocity
	var _temp_accel: float
	var _target: Vector3 = direction * _speed
	
	_temp_vel.y = 0
	if direction.dot(_temp_vel) > 0:
		_temp_accel = acceleration
		
	else:
		_temp_accel = deacceleration
	
	if not is_on_floor():
		_temp_accel *= air_control
	
	# Interpolation
	_temp_vel = _temp_vel.linear_interpolate(_target, _temp_accel * delta)
	
	velocity.x = _temp_vel.x
	velocity.z = _temp_vel.z
	
	# Make too low values zero
	if direction.dot(velocity) == 0:
		var _vel_clamp := 0.01
		if abs(velocity.x) < _vel_clamp:
			velocity.x = 0
		if abs(velocity.z) < _vel_clamp:
			velocity.z = 0


func jump() -> void:
	if _is_jumping_input:
		velocity.y = jump_height
		snap = Vector3.ZERO


func sprint(delta: float) -> void:
	var previousSpeed = _speed
#	if can_sprint():
#		_speed = sprint_speed
#		cam.set_fov(lerp(cam.fov, FOV * 1.05, delta * 8))
#		sprinting = true
#
#	else:
#		_speed = walk_speed
#		cam.set_fov(lerp(cam.fov, FOV, delta * 8))
#		sprinting = false

func updateSpeed():
	if isSlowed:
		var targetSpeed = walk_speed / 2.5
		_speed = lerp(_speed, targetSpeed, 0.1)
		if (_speed < targetSpeed + 0.01):
			isSlowed = false
	else:
		if (_speed < walk_speed):
			_speed = lerp(_speed, walk_speed, 0.05)


func can_sprint() -> bool:
	return (sprint_enabled and is_on_floor() and _is_sprinting_input and move_axis.x >= 0.5)
