function [] = crop_root(indir, fileprefix, nfile, cropdir,...
                            left, right, top, bottom, file_ext, ...
                            rotation_digits)
%   objective:  crop root from the image, remove the top of the bottle
%   need user interaction
%   input:  
%       indir:      input directory (should have slash '\' at the end)
%       fileprefix: prefix of image files
%       nfile:      #. image files
%       cropdir:    output directory (should have slash '\' at the end)

%-----------------------body
fprintf('crop images:\n');
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
    
    %img = rgb2gray(imread(infile));
    % 16 bits
    img = im2double(rgb2gray(imread(infile)));
    
    %outfile = get_filename([cropdir fileprefix], file, '.bmp');
    % first, get file name    
    outfile = get_filename(fileprefix, file, '.bmp',rotation_digits);          
    % second, make correct path
    outfile = fullfile(cropdir, outfile);  
    
    imwrite(img(top:bottom, left:right), outfile);
end
