function new_coords = rotatez(coords, theta)
% objective: rotate along y-axis
% input:
%   coords: original coordinates
%   theta:  angle to rotate counter-clockwise
% output:
%   new_coords: coordinates after rotation

%--------------  body
%   construct the rotation matrix
R = [cos(theta),-sin(theta),0;sin(theta),cos(theta),0;0,0,1];
new_coords = R*coords;
