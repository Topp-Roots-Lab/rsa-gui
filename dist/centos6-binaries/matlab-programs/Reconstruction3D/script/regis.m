function [left, right, top, bottom] = regis(indir, fileprefix, nfile, ...
                                            file_ext, rotation_digits)
%   objective:  read images, find the rotation
%   axis, and make the rotation axis the center of the images
%   input:  
%       indir:      input directory (should have slash '\' at the end)
%       fileprefix: prefix of image files
%       nfile:      #. image files

%----------------body
%   read images

%infile = get_filename([indir fileprefix], 1, ['.' file_ext]);
% first, get file name
infile = get_filename(fileprefix, 1, ['.' file_ext],rotation_digits);          
% second, make correct path
infile = fullfile(indir, infile);  
%disp(['infile=' infile]);
img = imread(infile);
addup = im2double(rgb2gray(img));

fprintf('reading image:\n');
for file = 1:nfile
    fprintf(' #%d', file);
    if mod(file, 20) == 0
        fprintf('\n');
    end

    %infile = get_filename([indir fileprefix], file, ['.' file_ext]);
    % first, get file name    
    infile = get_filename(fileprefix, file, ['.' file_ext],rotation_digits);          
    % second, make correct path
    infile = fullfile(indir, infile); 
    %disp(['infile=' infile]);
  
    img = imread(infile);
    img = im2double(rgb2gray(img));
    addup = min(addup, img);
end
fprintf('\n');

%addup = uint8(addup);
clear img file;

% % find align_left/align_right most pixel with gray level less than 100
% LOWGRAY = 100;
% RIGHT_BUTTON = 3;
% 
% while (true)
%     [b,a,button]= ginput(1);
%     if (button == RIGHT_BUTTON) break; end
%     row = round(a);
%     
%     align_left = 1;
%     while align_left<size(addup,2) && addup(row,align_left)>LOWGRAY
%         align_left = align_left+1;
%     end    
%     align_right = size(addup,2);
%     while align_right>1 && addup(row,align_right)>LOWGRAY
%         align_right = align_right-1;
%     end
%     
%     close;
%     imshow(addup); hold on;
%     x = ones(1,size(addup,1))*align_left;
%     y = [1:size(addup,1)];
%     line(x,y,'Color','r');
%     x = ones(1,size(addup,1))*align_right;
%     line(x,y,'Color','r');
% end

RIGHT_BUTTON = 3;
while true
    clf;
    imshow(addup);
    [b,a,button] = ginput(1);
    if button == RIGHT_BUTTON, continue; end
    top = round(a);
    x = [1:size(addup,2)];
    y = ones(1,size(addup,2))*top;
    line(x,y,'Color','b');

    [b,a,button] = ginput(1);
    if button == RIGHT_BUTTON, continue; end
    bottom = round(a);
    x = [1:size(addup,2)];
    y = ones(1,size(addup,2))*bottom;
    line(x,y,'Color','b');

    [b,a,button] = ginput(1);
    if button == RIGHT_BUTTON, continue; end
    left = round(b);
    x = ones(1,size(addup,1))*left;
    y = [1:size(addup,1)];
    line(x,y,'Color','b');

    [b,a,button] = ginput(1);
    if button == RIGHT_BUTTON, continue; end
    right = round(b);
    x = ones(1,size(addup,1))*right;
    y = [1:size(addup,1)];
    line(x,y,'Color','b');

    [b,a,button] = ginput(1);
    if button == RIGHT_BUTTON, continue; end
    break;
end
