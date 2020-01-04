function [matches, addup, best_choice] = find_rotation_axis(indir, ...
                                                   fileprefix, nfile, ...
                                                   outdir, rotation_digits)
%   objective:  read silhouettes (bmp format as default), find the rotation
%   axis, and make the rotation axis the center of the images
%   input:  
%       indir:      input directory (should have slash '\' at the end)
%       fileprefix: prefix of image files
%       nfile:      #. image files
%       outdir:     output directory (should have slash '\' at the end);

%----------------body
%   read silhouettes

% first, get file name
%infile = get_filename([indir fileprefix], 1, '.bmp');
infile = get_filename(fileprefix, 1, '.bmp',rotation_digits);          
% second, make correct path
infile = fullfile(indir, infile);

img = imread(infile);
imheight = size(img,1);
imwidth = size(img,2);
addup = false(imheight, imwidth);

fprintf('imheight=%d',imheight); 
fprintf('\n');
fprintf('imwidth=%d',imwidth); 
fprintf('\n');

fprintf('reading image:\n');
for file = 1:nfile
    fprintf(' #%d', file);
    if mod(file, 20) == 0
        fprintf('\n');
    end
    
    % first, get file name
    %infile = get_filename([indir fileprefix], file, '.bmp'); %default: bmp format
    infile = get_filename(fileprefix, file, '.bmp',rotation_digits);          
    % second, make correct path
    infile = fullfile(indir, infile);  
    
    img = imread(infile);
    
%     disp('before addup'); 
%     imheight2 = size(img,1);
%     imwidth2 = size(img,2);
%     fprintf('imheight2=%d',imheight2); 
%     fprintf('\n');
%     fprintf('imwidth2=%d',imwidth2); 
%     fprintf('\n');    
    
    addup = addup | img;

end
fprintf('\n');

% figure(1);
% imshow(addup); hold on;
clear img file;

%   find rotation axis
matches = zeros(1,imwidth);
for i = 1:imwidth
    ncol = min(i-1, imwidth-i);
    tmp1 = addup(:,i-ncol:i-1);
    tmp2 = addup(:,[i+ncol:-1:i+1]);
    matches(i) = sum(squeeze(addup(:,i))) + 2*sum(tmp1(:)==1 & tmp2(:)==1);
end
matches = matches/sum(addup(:));

% %   draw rotation axis
% middle_col = ceil(imwidth/2);
% line([middle_col, middle_col], [1 imheight], 'Color', 'b');
[best_matches, best_choice] = max(matches);
% line([best_choice best_choice], [1 imheight], 'Color', 'r');

fprintf('rotation parameters:\n');
fprintf('imwidth %d', imwidth);
fprintf('\n');
fprintf('best_choice %d', best_choice);
fprintf('\n');

%figure(2);
%plot(matches);
% 
% matches(best_choice)
% matches(middle_col)
% best_choice
% middle_col

%   write images
fprintf('writing image:\n');
ncol = min(best_choice-1, imwidth-best_choice);
for file = 1:nfile
    fprintf(' #%d', file);
    if mod(file, 20) == 0
        fprintf('\n');
    end    

    %infile = get_filename([indir fileprefix], file, '.bmp'); %default: bmp format
    % first, get file name    
    infile = get_filename(fileprefix, file, '.bmp',rotation_digits);          
    % second, make correct path
    infile = fullfile(indir, infile);  
    
    img = imread(infile);
    
    %outfile = get_filename([outdir fileprefix], file, '.bmp'); %default: bmp format
    % first, get file name    
    outfile = get_filename(fileprefix, file, '.bmp',rotation_digits);          
    % second, make correct path
    outfile = fullfile(outdir, outfile);  
    
    imwrite(img(:,best_choice-ncol:best_choice+ncol), outfile);
end
fprintf('\n');
