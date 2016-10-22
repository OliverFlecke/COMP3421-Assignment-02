# COMP3421 Assignment 02

## Running the game
The game is run in the same way as described in the assignment specifications. Just run the main method in the Game class, which expects a path to the level file. 

As enemies has been added to the game, these can be specified in the level file similar to how the trees are specified.

```
"enemies" : [
    {
        "x" : 4,
        "z" : 4,
    },
    {   
        "x" : 4,
        "z" : 2
    }
]
```

## Supported extestions
Different extensions has been added to the game. 
A slightly complex avatar. (Not that complex)
A night mode (still missing torch)
Fixed roads. This is still not working for all cases. The egdes of the roads are at the correct height, but the middle gets interpolated between these points. 
Rain using a particle system. There is no actual creation and destruction of the particle objects, but when the particle hits the ground, it gets reset and starts falling from the top of the world. 
Moving sun. The sun is move around the world at the same height all the time, instead of having sunset and sunrise. This is easy changed by switching the axis which the sun rotated about. The light which the sun emits changes according to the time of the day (from all black to nearly all white). 


## Keyboard short-cuts to active/deactivate features
- R: Reset the avatar to original postion
- L: Switch the sun between a static and moving mode
- D: Set sun to day mode
- N: Set sun to night mode (It will still be visible in the sky, but it won't emit any light)
- S: Switch the dynamic time on and off. When on, this will make the light from the sun go from day mode to night mode.
- C: Switch between rain modes. (Note: rain is not active in sun view mode)

- Arrow keys: Move around. The left and right key are staffing left and right, instead of rotating the avatar.
- Mouse: Use the mouse to rotate the camera to look around. This is best done by dragging the mouse. (The ,AOE keys can also be used to rotate the camera around)
- Shift: Sprint, make the avatar move and rotate faster. 
- V: Change between first person, thrid person, and sun view mode. 

