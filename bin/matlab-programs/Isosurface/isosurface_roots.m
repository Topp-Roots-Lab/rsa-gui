function rootstats = isosurface_roots(x,scale)
% function rootstats = isosurface_roots(x,scale)
%
% Developed by Joshua Weitz, 2013
% to be integrated into the Duke pipeline
%
% isosurface_roots is a function that takes as input a
% set of voxels, in "x", of a root system.  Hence x should bae
% a N x 3 matrix of non-negative integers. The scale
% is in units of length per pixel, e.g. 0.2 mm/pixel
% and returns a single structure "rootstats" that contains the following fields:
%   area (mm^2)
%   volume (mm^3)
% Because there are many isosurfaces, the method creates a
% series of isosurfaces that "shrink wrap" the volume and then
% returns the area in the limit that the shrink wrap covers the existing
% voxel set.
% 
% The method is one of generating a surface mesh using the
% isosurface command in Matlab, which utilizes a marching cubes algorithm.
% Then, the area of the mesh is calculated using a standard approach
% called Heron's algorithm.  The area calculation was downloaded
% from Matlab File Central - it is included as a subfunction here.

% Simple volume calculation based on summation of each voxel volume
rootstats.volume=length(x)*scale^3;

% Area calculation involving isosurfaces
% Ensure we don't cross 0 when we make isosurfaces by 
% moving points away from 0 and by buffering the bounding box
% in our 3D reconstruction
x=x+2;
bbx_min=min(x);
bbx_max=max(x)+2;
xind=sub2ind(bbx_max,x(:,1),x(:,2),x(:,3));
spixels=zeros(bbx_max);
vpixels=zeros(bbx_max);
vpixels(xind)=1;  % vpixels is now a volume with 1-s where the root is, and 0 otherwise

% Method isosurface
isos=[0.95:0.005:0.995];
for i=1:length(isos),
  [f,v]=isosurface(vpixels,isos(i));
  a(i)=aI(f,v);
end

% Interpolate the area as isos->1, i.e., a surface that touches
% the root voxels, rather than the single voxel layer outside of the root.
x=isos;
y=a;
ymean=mean(y);
xmean=mean(x);
sxy=sum((y-ymean).*(x-xmean))/(length(x)-1);
sx2=sum((x-xmean).^2)/(length(x)-1);
bhat=sxy/sx2;
ahat=ymean-bhat*xmean;
rootstats.area=(ahat+bhat*1)*scale^2;
rootstats.fit_area_error=sqrt(sum((y-ymean).^2)/(length(y)-2))/ymean;
rootstats.fit_area_error_note='This is a relative error per degree of freedom, less than 0.01 (e.g., 1%) could be a good indicator of suitability of the extrapolation';

%function [A]= localareaIsosurface(F,V)
%Function to calculate the area of an isosurface generated by MATLAB's
%   built-in isosurface().
%SCd 07/12/2010
%
%This function uses Heron's numerically stable formula available here:
%>>web('http://en.wikipedia.org/wiki/Heron''s_formula','-new');
%
%Input Arguments:
%   [F,V] = isosurface(...);   
%   F: calculation above
%   V: calculation above
%   
%Output Arguments:
%   A: surface area of the triangulated isosurface.
%
% Copyright (c) 2009, Daniel Siderius 
% All rights reserved.
% 
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are
% met:
% 
%     * Redistributions of source code must retain the above copyright
%       notice, this list of conditions and the following disclaimer.
%     * Redistributions in binary form must reproduce the above copyright
%       notice, this list of conditions and the following disclaimer in
%       the documentation and/or other materials provided with the distribution
% 
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
% IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
% ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
% LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%
%      %Calculate side lengths:
%      sides = zeros(size(F,1),3); %Preallocate
%      sides(:,1) = sqrt(... %a
%          (V(F(:,1),1)-V(F(:,2),1)).^2+...
%          (V(F(:,1),2)-V(F(:,2),2)).^2+...
%          (V(F(:,1),3)-V(F(:,2),3)).^2);
%      sides(:,2) = sqrt(... %b
%          (V(F(:,2),1)-V(F(:,3),1)).^2+...
%          (V(F(:,2),2)-V(F(:,3),2)).^2+...
%          (V(F(:,2),3)-V(F(:,3),3)).^2);
%      sides(:,3) = sqrt(... %c
%          (V(F(:,1),1)-V(F(:,3),1)).^2+...
%          (V(F(:,1),2)-V(F(:,3),2)).^2+...
%          (V(F(:,1),3)-V(F(:,3),3)).^2);
%      %Sort so: sides(:,1)>=sides(:,2)>=sides(:,3).
%      sides = sort(sides,2,'descend');
%      %Calculate Area!
%      A = sum(sqrt(...
%          (sides(:,1)+(sides(:,2)+sides(:,3))).*...
%          (sides(:,3)-(sides(:,1)-sides(:,2))).*...
%          (sides(:,3)+(sides(:,1)-sides(:,2))).*...
%          (sides(:,1)+(sides(:,2)-sides(:,3)))))/4;
%  end
%
%end