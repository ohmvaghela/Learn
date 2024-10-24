# Core-Interview

# Eyantra robotics competitions

- CoppeliaSim(V-REP)
- Solidworks
- Lua

## Stering and speed control
- Error : Cross-Track Error; Angle Error
- Using these two error we found Sterring control 
- And depending on the steering angle there was speed
    - Higher Steering angle -> Low speed
- [link](https://ai.stanford.edu/~gabeh/papers/hoffmann_stanley_control07.pdf)
- [link](https://dingyan89.medium.com/three-methods-of-vehicle-lateral-control-pure-pursuit-stanley-and-mpc-db8cc1d32081)
- [link](https://journals.sagepub.com/doi/epub/10.1177/1729881420974852)

- [youtube](https://youtu.be/HKetiTpKIkg)
- [youtube](https://youtu.be/xUEcDVXSVzc)
- Similarly with this data we find future position and with this future position we find next steering angle and speed 
    - This iteration was done 5 times with time-interval found using hit and trial

## Gyro Wheel
- LRQ controller, Input variable :
    - Bike angle and angular speed
    - Gyro angle and speed
- Control output is torque
    - Torque = weight*Bike angle + weight*Bike angular speed + weight*Gyro Angle + weight*Gyro Angular speed

## Arm controller
- IK library

# Flipkart Grid

## OpenCv
### Bot detection
- Mask (HSV)
- Morpological operation 
- Contour

### Stage Detection
- Mask
- Morphological operation
- Canny edge detection
- Find contour

# CTT
- CTT data why?
- Stastical analysis (Kurtosis, Skewness, Weighted avg)
- Time domain analysis
    - TISEAN package (Non linear dynamically determinsitic system)
        - Non Caotic
        - 6 Variable dependent

# ROS
- Nodes : Executables using ros to communicate with each other
- Message : ROS data type
- Topic : Channel over which nodes exchange data (Many to Many)
- Master : Naming and registeration services
- rosout : Console log for ros
- roscore : Master + rosout + paramServer

# ROS navigation
- Gazebo World, URDF, Robot state publisher, joint state publisher
- Gmapping 
- MoveBase (Cmd_vel, odom, global and local planner params)  
