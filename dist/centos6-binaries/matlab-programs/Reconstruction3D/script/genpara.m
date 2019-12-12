function [Rs, Ts] = genpara(nimgs, parafile)
% objective: generate extrinsic parameters
% input:
%   nimgs:      #. images
%   parafile:   output file
% output:
%   Rs:         rotation matrix (n by 3 by 3)
%   Ts:         translation matrix (n by 3)

%----------- body
Rs = zeros(nimgs,3,3);
r(1,:) = [0 0 -1];
r(2,:) = [0 1 0];
r(3,:) = [1 0 0];
Rs(1,:,:) = r;

theta = 0;
for i = 2:nimgs
    theta = theta - 2*pi/nimgs;
    Rs(i,1,:) = rotatez(r(1,:)',theta);
    Rs(i,2,:) = rotatez(r(2,:)',theta);
    Rs(i,3,:) = rotatez(r(3,:)',theta);
end

fid = fopen(parafile, 'w');
for i = 1:nimgs
    fprintf(fid, '%f %f\n', Rs(i,2,1), Rs(i,3,1));
end
fclose(fid);
