% 3D reconstruction program
% 
% Input params:
%       dataDir - full path for the folder where image set is located
%
% Image set has the following structure (names are predetermined):
%      - images 
%      - crop
%      - config.xml
% images - folder, where original files located (usually: jpg, tiff)
% crop   - folder, where cropped images are located
% config.xml defines parameters for running rootwork program.
%
% If crop folder is populated (cropping is done), then rootwork can skip 
% cropping step, there is no need for image folder. Otherwise a manual 
% intervention is needed for the cropping.
%
%
% Output
% Depending on configuration parameters (see config.xml), result varies. 
% But, in general, output folders, such as root,silhouette,
% silhouette2,silhouette3 and file with 3D reconstruction
% result would appear.
%
% NOTE: When installing this program to Server, change rootworkDir
% parameter (see below) properly.
%
% Developers
%
% Original matlab code and algorithms used in rootwork program 
% are developed by Ying Zheng, yuanqi@cs.duke.edu
%
% Further code modifications (including those needed for using rootwork program on Unix Server)
% are made by Vladimir Popov, vladimir.popov@duke.edu
%
%

function [] = rootwork(dataDir,configFile,crop)

disp('rootwork starts ...'); 

% Set configuration parameter - folder where rootwoork programm 
% is currently installed.

%----------------------------------------------------------------
% MATLABPATH variable is set on the script
%
% In config file put full path for the output voxels file - see
% the <output-file-name> tag
%
% %-----------------------------------------------------------------
%  rootworkDir = fullfile('/localhome','vp23','RootArchitecture', ...
%                         'test','v3_matlab_deploy_2');  
% %-----------------------------------------------------------------

% tw 2015feb6 text dir
rootworkDir = fullfile('/home','twalk','roots3d','RSA-Gia','usr','local','bin', ...
                          'matlab-programs','Reconstruction3D');  

%rootworkDir = fullfile('/usr','local','bin', ...
%                          'matlab-programs','Reconstruction3D');  

p=genpath(fullfile(rootworkDir,'admin'));
addpath(p); 
p=genpath(fullfile(rootworkDir,'recon_hysteresis','Release'));
addpath(p); 
p=genpath(fullfile(rootworkDir,'script'));
addpath(p); 
p=genpath(fullfile(rootworkDir,'script','laplaceEq'));
addpath(p);  

if ~exist('dataDir')
   error('usage: rootwork(dataDir) -- dataDir is image set folder');
   %disp('usage: rootwork(dataDir) -- dataDir is image set folder');
   %exit;
end

% % configFileName
% configFileName = fullfile(dataDir, 'config.xml');

if ~exist('configFile') || isempty(configFile)
    configFileName = fullfile(dataDir, 'config.xml');    
else
    configFileName = configFile;    
end

if ~exist('crop')
    crop = '';    
end

% show params, if needed  
disp(['rootworkDir=',rootworkDir]);  
disp(['configFileName=',configFileName]);
disp(['crop=',crop]);

try
    % get parameters from configuration file
    [doCrop, ...
     useGiaroots_crop ...
     doLikelihoodImg, ... 
     doSilhouette_hysteresis, ... 
     useGiaroots_thresholding, ...
     doSilhouette_harmonic, ... 
     doAxis, ... 
     doReconstruct_hysteresis, ... 
     file_ext, ... 
     file_ext_giaroots_crop, ...      
     file_ext_giaroots_thresh, ...       
     filePrefix, ... 
     silhouetteForDoAxis, ... 
     numImgs, ... 
     numImgUsed, ... 
     resolution, ... 
     numNodesOnOctree, ... 
     sil_upper_threshold, ... 
     sil_lower_threshold, ... 
     harmonic_threshold, ... 
     recon_upper_threshold, ... 
     recon_lower_threshold, ... 
     distortion_radius, ... 
     num_components, ...      
     recon_opt, ...
     recon_filename, ...
     output_file, ...
     extraInfo, ...
     ref_image, ...
     ref_ratio] = getConfig( configFileName );
 
     %pixel_expansion, ...  % not used
     %ref_index, ... % not used
 
    % show parameters taken from the configuration file, if needed    
    showConfigParams(doCrop, ...
                     useGiaroots_crop, ...
                     doLikelihoodImg, ... 
                     doSilhouette_hysteresis, ... 
                     useGiaroots_thresholding, ...
                     doSilhouette_harmonic, ... 
                     doAxis, ... 
                     doReconstruct_hysteresis, ... 
                     file_ext, ... 
                     file_ext_giaroots_crop, ...      
                     file_ext_giaroots_thresh, ... 
                     filePrefix, ...
                     silhouetteForDoAxis, ... 
                     numImgs, ... 
                     numImgUsed, ... 
                     resolution, ... 
                     numNodesOnOctree, ... 
                     sil_upper_threshold, ... 
                     sil_lower_threshold, ... 
                     harmonic_threshold, ... 
                     recon_upper_threshold, ... 
                     recon_lower_threshold, ... 
                     distortion_radius, ... 
                     num_components, ...                      
                     recon_opt, ... 
                     recon_filename, ...
                     output_file, ...
                     extraInfo, ...                     
                     ref_image, ...                     
                     ref_ratio);  
                     
    %pixel_expansion, ... % not used   
    %ref_index, ...  % not used
                     

     if (recon_opt == 1 || recon_opt == 2 || recon_opt == 5)
        disp(['recon_opt setting is Ok']);
     else
        error('For this program, <recon-option> in the config file should be 1,2 or 5');
        %disp('For this program, <recon-option> in the config file should be 2');
        %exit;
     end  
     
     if  (recon_opt == 5) 
        msg = ['Currently, <recon-option> is set to 5 (create STL file).'...  
        ' Make sure that the voxels file is already created.' ...   
        ' The following settings would be used:' ... 
        ' <do-crop>0</do-crop>' ... 
        '<use-giaroots-crop>0</use-giaroots-crop>' ...
        '<do-likelihood-img>0</do-likelihood-img>' ... 
        '<do-silhouette-hysteresis>0</do-silhouette-hysteresis>' ... 
        '<use-giaroots-thresholding>0</use-giaroots-thresholding>' ...        
        '<do-silhouette-harmonic>0</do-silhouette-harmonic>' ... 
        '<do-axis>0</do-axis>' ... 
        '<do-reconstruct-hysteresis>1</do-reconstruct-hysteresis>'];
        disp(msg);
        doCrop = 0; 
        useGiaroots_crop = 0;
        doLikelihoodImg = 0;
        doSilhouette_hysteresis = 0;
        useGiaroots_thresholding = 0;        
        doSilhouette_harmonic = 0;
        doAxis = 0;
        doReconstruct_hysteresis = 1;
     end     
     
  % recon_upper_threshold parameter, for some reason, is not taken 
  % directly from config file. 
  % recon_upper_threshold parameter is set to value of the numImgUsed
  recon_upper_threshold = numImgUsed;
 
  % set dirs based on the rootworkDir - rootwork installation folder
  %reconDir = fullfile(rootworkDir, 'recon_hysteresis','Release');
  binDir = fullfile('/usr','local','bin');
  reconDir = fullfile(binDir, 'reconstruction3D');
  recon_file = fullfile(reconDir,'Reconstruction');
  reconStlDir = fullfile(binDir, 'reconstruction3D-stl');
  recon_file_v4_STL = fullfile(reconStlDir,'recon-v4-stl');
  paraFileName = fullfile(rootworkDir, 'para.txt');
 
  % image extension used within this program
  imgExtension = '.bmp';  
  
  % set dirs based on the dataDir - image set folder
  inputDir                = fullfile(dataDir, 'img');
  cropDir                 = fullfile(dataDir, 'crop');
  rootImgDir              = fullfile(dataDir, 'root');
  silDir                  = fullfile(dataDir, 'silhouette');
  silDir2                 = fullfile(dataDir, 'silhouette2');
  silDir3                 = fullfile(dataDir, 'silhouette3'); 
  
  if silhouetteForDoAxis==1
      doAxisInputDir = silDir;
  else 
      doAxisInputDir = silDir2;
  end   

  % show paths, if needed  
  disp(['reconDir=',reconDir]);
  disp(['paraFileName=' paraFileName]);
  disp(['inputDir=' inputDir]);  
  disp(['cropDir=' cropDir]);  
  disp(['rootImgDir=' rootImgDir]);
  disp(['silDir=' silDir]);  
  disp(['silDir2=' silDir2]);
  disp(['silDir3=' silDir3]); 
  disp(['doAxisInputDir=' doAxisInputDir]); 

%
% check whether one or two zero are used for image rotation number 
% and set ROTATION_DIGITS parameter.
% Formally, ROTATION_DIGITS parameter is put in all section, inlcuding
% doCrop, doLikelihoodImg, etc., in order to fit function signatures.
% But it works only for if Gia-Root thresholding used.
%
% In order to check, find the image for rotation #1 
% in gia-roots thresholded folder and try two options:
% for two digits and three digits rotation number.
% 
% this is where the gia-roots thresholded images should be located
threshDir = fullfile(silDir, 'thresholding');
% get the image name, suggesting 2 digits format:
infile_2 = get_filename(filePrefix, 1, ['.' file_ext_giaroots_thresh],2);
disp(['infile_2=' infile_2]);
% get the image name, suggesting 3 digits format:
infile_3 = get_filename(filePrefix, 1, ['.' file_ext_giaroots_thresh],3);
disp(['infile_3=' infile_3]);
% find rotation digits format;
 ROTATION_DIGITS=-1;
 if (exist(threshDir, 'dir')) 
    if exist(fullfile(threshDir, infile_2),'file') 
        ROTATION_DIGITS=2;
    elseif exist(fullfile(threshDir, infile_3),'file')
        ROTATION_DIGITS=3;
    else
        msg= ['Neither file' ' ' ...
               infile_2 ' ' ...
              'nor file', ' ' ...
               infile_3 ' ' ...              
              'found.'];
        disp('Image rotation digits format NOT found ...');          
        fprintf('ROTATION_DIGITS=%d\n', ROTATION_DIGITS); 
        error(msg);        
    end    
 else
    msg= ['This version of Rootwork program works only' ' ' ...
          'with Gia-Roots thresholding.' ' ' ...
          'No Gia-Roots thresholding folder' ' ' ...
           threshDir, ' ' ...
          'found.'];
    error(msg);
 end 
 
 disp('Image rotation digits format found ...');
 fprintf('ROTATION_DIGITS=%d\n', ROTATION_DIGITS); 
  
  if doCrop
      fprintf('crop images\n');
      if ~exist(cropDir, 'dir')
          mkdir(cropDir);
      else
          delete(fullfile(cropDir,'*'));
      end
      [left, right, top, bottom] = regis(inputDir, filePrefix, numImgs, ... 
                                         file_ext, ROTATION_DIGITS); 
      crop_root(inputDir, filePrefix, numImgs, cropDir,...
                left, right, top, bottom, file_ext, ...
                ROTATION_DIGITS);
      close all;
  end
  
  if strcmp(char(crop),'only')
      fprintf('cropping finished.\n');
      return;
  end   
 
  %===================================================================
  % Decription of the usage of the giaroots crop images
  %===================================================================
  % Giaroots crop images are loaded into the cropDir folder.
  %
  % This means that we simply substitute crop images (produced usually by
  % crop algorithm in this program) by giaroots crop images
  %
  % 
  imgExtensionCrop=imgExtension;
  if useGiaroots_crop      
      imgExtensionCrop=['.' file_ext_giaroots_crop];
  end    
  
  if doLikelihoodImg
      fprintf('compute likelihood images:\n');
      if ~exist(rootImgDir, 'dir')
          mkdir(rootImgDir);
      else
          % in this case the images in the cropDir folder do not change from
          % run to run, because crop procedure of this program is not used.
          % So, in the case of using giaroots crop images,
          % do not delete the rootImgDir folder if it is already there          
          if ~useGiaroots_crop
            delete(fullfile(rootImgDir, '*'));
          end
      end
      for file = 1 : numImgs
          fprintf(' #%d', file);
          if mod(file, 20) == 0
              fprintf('\n');
          end
          %infile = get_filename([cropDir filePrefix], file, imgExtension);
          % first, get file name
          infile = get_filename(filePrefix, file, imgExtensionCrop, ...
                                ROTATION_DIGITS);          
          % second, make correct path
          infile = fullfile(cropDir, infile);
          disp(['infile=',infile]);
          
          % original line of code
          % does not work for crop images from gia-roots manual crop
          % (after converting from jpg to bmp with ImageMagick)
          inputimg = imread(infile);      %gray image
        
          % add to fix gia-roots input - see below
          n = ndims(inputimg);
          if n == 2 
            msg='inputimg dimension %d';
            fprintf(msg, n); 
            fprintf('\n');          
          %-------
          % hack
          %------------------------          
          % works for crop images from gia-roots manual crop
          % (after converting from jpg to bmp with ImageMagick)-
          % converted bmp files, when read by imread,
          % have three dimesions. Looks like that rgb2gray function
          % makes the needed (by medfilt2 function) two dimensions
          elseif n == 3 
            msg=['Reading gia-roots image; inputimg dimension = %d'];
            fprintf(msg, n);
            fprintf('\n');              
            inputimg=rgb2gray(inputimg);
            n = ndims(inputimg);
            msg=['After applying rgb2gray function to imputimg,' ...
                 'inputimg dimension = %d'];
            fprintf(msg, n);
            fprintf('\n');
          %------------------------  
          else
            msg= 'Unusual inputimg dimension = %d';
            fprintf(msg, n);
            fprintf('\n');
          end 
         
          inputimg = medfilt2(inputimg);  %median filter
          histogram = imhist(inputimg);
          sumHist = cumsum(histogram);
          numPixels = max(sumHist(:));
          rootImg = sumHist( inputimg(:,:) + 1 );
          rootImg = rootImg / numPixels;
          rootImg = 1 - rootImg;
          rootImg = medfilt2(rootImg);          

          % first, get file name
          outfile = get_filename(filePrefix, file, imgExtension, ...
                                 ROTATION_DIGITS);          
          % second, make correct path
          outfile = fullfile(rootImgDir, outfile);          
          imwrite(rootImg, outfile);
      end
  end
  
  % imhysteresis makes silhouettes from the likelihood images 
  % in the rootImgDir folder and save them in silDir
  % folder as 0 and 1
  if doSilhouette_hysteresis
      if ~exist(silDir, 'dir')
          mkdir(silDir);
      else
          delete(fullfile(silDir,'*'));
      end    
      fprintf('extract silhouettes:\n');
      for file = 1 : numImgs
          fprintf(' #%d', file);
          if mod(file, 20) == 0
              fprintf('\n');
          end
          %infile = get_filename([rootImgDir filePrefix], file,imgExtension);
          % first, get file name
          infile = get_filename(filePrefix, file, imgExtension, ....
                                ROTATION_DIGITS);          
          % second, make correct path
          infile = fullfile(rootImgDir, infile);
          
          %disp(['infile=' infile]);  
          
          rootImg = im2double(imread(infile));        

          % first, get file name          
          outfile = get_filename(filePrefix, file, imgExtension, ... 
                                 ROTATION_DIGITS);          
          % second, make correct path
          outfile = fullfile(silDir, outfile);
          
          %disp(['outfile=' outfile]); 
          
          imhysteresis(rootImg, sil_upper_threshold, ...
                       sil_lower_threshold, outfile);
      end
  end
  
  %===================================================================
  % Decription of the usage of the giaroots thresholded images
  %===================================================================
  % Giaroots thresholded images are loaded into the silDir folder.
  %
  % This means that we simply substitute silhouettes images (produced
  % usually by hysteresis algorithm in this program) by the giaroots 
  % thresholded images
  %
  % The thresholded images are rewritten and saved in the same silDir
  % folder as 0 and 1, which is needed for finding rotation axis
  % (see "if doAxis ..." code block below). 
  % (Currently, the giaroots thresholded images have only 0 and 254 
  % number inside - to process them properly, there is a need to have
  % only 0 and 1)
  %
  % After rotation axis procedure,the result is saved in the silDir3 folder.
  % Reconstruction code (see "if doReconstruct_hysteresis .." below)
  % formally uses images in both silDir and silDir3 folders, though,
  % accordeing to code, only the first image in the silDir folder 
  % is neeeded (for finding the nRows, nCols).
  
  % this code opens all giaroots thresholded images and saves them as
  % 0 and 1 files for further processing (now saved as bmp format; giaroots
  % thresholded images are kept untouched).
  % useGiaroots_thresholding = 0; 
  imgExt_giaroots_thresh=['.' file_ext_giaroots_thresh];
  if useGiaroots_thresholding
  % in this case the images in the silDir folder do not change from
  % run to run, because sil_upper_threshold and sil_lower_threshold
  % settings are not used.
  % So, do not delete the silDir folder if it is already there
      if ~exist(silDir, 'dir')
          mkdir(silDir);
%       else
%           delete(fullfile(silDir,'*'));
      end    
      fprintf('converting gia-roots threshoding files to 0 and 1 files ...\n');
      for file = 1 : numImgs
          fprintf(' #%d', file);
          if mod(file, 20) == 0
              fprintf('\n');
          end
          %infile = get_filename([rootImgDir filePrefix], file,imgExtension);
          % first, get file name
          infile = get_filename(filePrefix, file, imgExt_giaroots_thresh,...
                                ROTATION_DIGITS);  
          
          % this is where the gia-roots thresholded images are located
          threshDir = fullfile(silDir, 'thresholding');
          % second, make correct path
          % infile = fullfile(silDir, infile);
          infile = fullfile(threshDir, infile);
          
          %disp(['infile=' infile]);  
          
          giaroots_threshImg = im2double(imread(infile));        

          % first, get file name          
          outfile = get_filename(filePrefix, file, imgExtension, ...
                                 ROTATION_DIGITS);          
          % second, make correct path
          outfile = fullfile(silDir, outfile);
          
          %disp(['outfile=' outfile]); 
          
          giaroots_thresholding(giaroots_threshImg, outfile);
      end
  end    
  
  if doSilhouette_harmonic
      if ~exist(silDir2, 'dir')
          mkdir(silDir2);
      else
          delete(fullfile(silDir2,'*'));
      end    
      fprintf('extract silhouettes:\n');
      for file = 1 : numImgs
          fprintf(' #%d', file);
          if mod(file, 20) == 0
              fprintf('\n');
          end
          % first, get file name          
          infile = get_filename(filePrefix, file, imgExtension, ...
                                ROTATION_DIGITS);          
          % second, make correct path
          infile = fullfile(rootImgDir, infile);
          
          %disp(['infile=' infile]);  

          rootImg = im2double(imread(infile));
  
          % first, get file name          
          maskfile = get_filename(filePrefix, file, imgExtension, ...
                                  ROTATION_DIGITS);          
          % second, make correct path
          maskfile = fullfile(silDir, maskfile);         
          
          %disp(['infile=' infile]);  
          
          mask = imread(maskfile);
          msk1 = mask(1,:) == 0;
          nonRootPixelValues = rootImg(1,:);
  %         nonRootPixelValues = nonRootPixelValues(msk1);
          for i = 2:length(nonRootPixelValues)
              if msk1(i)==0
                  nonRootPixelValues(i) = ...
                                 mean(nonRootPixelValues(max(1,i-20):i-1));
              else
                  nonRootPixelValues(i) = ...
                                 mean(nonRootPixelValues(max(1,i-20):i));
              end
          end
              
          f = rootImg;
          f = imresize(f,0.1);
          f(1,:) = imresize(nonRootPixelValues, [1 size(f,2)]);
          f(2,:) = f(1,:);
          w = zeros(size(f));
          w = w==1;
          w(:,1) = 1; w(:,end) = 1;
          w(1,:) = 1; w(end,:) = 1;
          b = f.*w;
          hmax = 5;   % Upper bound on edge length inthe triangulation
          [h, d] = harmonic(f, w, hmax, hmax);
          h = h(2:end-1,2:end-1);
          background_threshold = min(imresize(h,size(rootImg))+ ...
                                 harmonic_threshold, sil_lower_threshold);          
           
          % first, get file name          
          outfile = get_filename(filePrefix, file, imgExtension, ...
                                 ROTATION_DIGITS);          
          % second, make correct path
          outfile = fullfile(silDir2, outfile);
          
          %disp(['outfile=' outfile]);           
          
          imhysteresis2(rootImg, sil_upper_threshold, ...
                        background_threshold, outfile);
      end
  end
  
  if doAxis
      fprintf('calculate rotation axis\n');
      if ~exist(silDir3, 'dir')
          mkdir(silDir3);
      else
          delete(fullfile(silDir3,'*'));
      end
      [matches, addup, best_choice] = ...
          find_rotation_axis(doAxisInputDir, filePrefix, numImgs, ...
                             silDir3,ROTATION_DIGITS);
  end
  
  if doReconstruct_hysteresis
    silDir3_plus_filePrefix = fullfile(silDir3,filePrefix);
    
    if recon_opt == 1
        infile = get_filename(silDir3_plus_filePrefix, 1, imgExtension, ...
                              ROTATION_DIGITS);
        img = imread(infile);
        nRows = size(img, 1);
        nCols = size(img, 2);

        disp('generate parameter file...');
        genpara(numImgs, paraFileName);
        disp('start 3D reconstruction...');  
        disp([recon_file ' 1 ' num2str(nCols) ' ' ...
            num2str(nRows) ' ' ...
            num2str(numNodesOnOctree) ' '...
            silDir3_plus_filePrefix ' ' ...
            num2str(numImgUsed) ' ' ...
            paraFileName ' ' num2str(resolution) ' ' ...
            num2str(distortion_radius) ' ' ...
            num2str(num_components) ' ' ...
            recon_filename ' ' ...
            num2str(extraInfo) ' ' ...
            num2str(ROTATION_DIGITS) ' ' ...
            num2str(ref_image) ' ' ...
            num2str(ref_ratio)]);   
            
        dos([recon_file ' 1 ' num2str(nCols) ' ' ...
            num2str(nRows) ' ' ...
            num2str(numNodesOnOctree) ' '...
            silDir3_plus_filePrefix ' ' ...
            num2str(numImgUsed) ' ' ...
            paraFileName ' ' num2str(resolution) ' ' ...
            num2str(distortion_radius) ' ' ...
            num2str(num_components) ' ' ...
            recon_filename ' ' ...
            num2str(extraInfo) ' ' ...
            num2str(ROTATION_DIGITS) ' ' ...
            num2str(ref_image) ' ' ...
            num2str(ref_ratio)]);           
             
          unix([recon_file_v4_STL ' 5 ' recon_filename ' ' ...
              output_file ' '  num2str(extraInfo)]);       
        
    elseif recon_opt == 5
        %For STL, use EXE for v4 version for the time being
          %recon_file_v4_STL = fullfile(reconDir,'recon-v4-stl');          
          unix([recon_file_v4_STL ' 5 ' recon_filename ' ' ...
              output_file ' '  num2str(extraInfo)]);        
    else        
        disp(['<recon-option> currently used not allowed for this program']);              
    end
  end

catch exc
   error('Error occured.Error message: %s.', exc.message);
   %fprintf('Error occured.Error message: %s.', exc.message);
   %fprintf('\n');
   %exit;
end

fprintf('finished.\n');

%exit;
end
