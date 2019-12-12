function [ doCrop useGiaroots_crop ...
           doLikelihoodImg doSilhouette_hysteresis ... 
           useGiaroots_thresholding ...
           doSilhouette_harmonic doAxis doReconstruct_hysteresis ...
           file_ext  file_ext_giaroots_crop file_ext_giaroots_thresh ...
           filePrefix silhouetteForDoAxis ...
           numImgs numImgUsed resolution numNodesOnOctree ...
           sil_upper_threshold sil_lower_threshold ...
           harmonic_threshold recon_upper_threshold recon_lower_threshold ...
           distortion_radius num_components ...
           recon_opt recon_filename output_file extraInfo ...
           ref_image ref_ratio] ...           
           = getConfig( ConfigFileName )
       
    
       %   pixel_expansion ref_image ref_index ref_ratio] ...        
       
       
    % 
    % Error, if any, is caught in the main procedure - rootwork
    %
    docXml = xmlread(ConfigFileName);

    doCrop = str2num(getConfigValue(docXml,'do-crop'));
    useGiaroots_crop = str2num(getConfigValue(docXml,'use-giaroots-crop'));
    doLikelihoodImg = str2num(getConfigValue(docXml,'do-likelihood-img'));
    doSilhouette_hysteresis = str2num(getConfigValue(docXml,'do-silhouette-hysteresis'));
    useGiaroots_thresholding = str2num(getConfigValue(docXml,'use-giaroots-thresholding')); 
    doSilhouette_harmonic = str2num(getConfigValue(docXml,'do-silhouette-harmonic'));
    doAxis = str2num(getConfigValue(docXml,'do-axis'));
    doReconstruct_hysteresis = str2num(getConfigValue(docXml,'do-reconstruct-hysteresis'));

    file_ext = getConfigValue(docXml,'extension-root-images');
    file_ext_giaroots_crop = getConfigValue(docXml,'extension-giaroots-crop-images');  
    file_ext_giaroots_thresh = getConfigValue(docXml,'extension-giaroots-thresh-images');

    filePrefix = getConfigValue(docXml,'file-prefix'); 
    silhouetteForDoAxis = str2num(getConfigValue(docXml,'silhouette-for-do-axis'));         

    numImgs = str2num(getConfigValue(docXml,'num-imgs'));  
    numImgUsed = str2num(getConfigValue(docXml,'num-img-used'));  
    resolution = str2double(getConfigValue(docXml,'resolution'));                    
    numNodesOnOctree  = str2num(getConfigValue(docXml,'num-nodes-on-octree')); 
    
    sil_upper_threshold = str2double(getConfigValue(docXml,'sil-upper-threshold'));
    sil_lower_threshold = str2double(getConfigValue(docXml,'sil-lower-threshold'));          
    harmonic_threshold = str2double(getConfigValue(docXml,'harmonic-threshold'));
    recon_upper_threshold = numImgUsed;
    recon_lower_threshold  = str2double(getConfigValue(docXml,'recon-lower-threshold')); 
    distortion_radius = str2num(getConfigValue(docXml,'distortion-radius'));
    num_components  = str2num(getConfigValue(docXml,'number-of-components'));
    
    recon_opt = str2num(getConfigValue(docXml,'recon-option')); 
    recon_filename = getConfigValue(docXml,'output-file-name'); 
    output_file = getConfigValue(docXml,'output-file-stl');
    extraInfo = getConfigValue(docXml,'extra-info');  
    
    % before deployment - update Gia-Roots GUI !!!    
    % adding new settings to config.xml 
    %pixel_expansion = str2num(getConfigValue(docXml,'pixel-expansion'));
    ref_image = getConfigValue(docXml,'ref-image');
    %ref_index = str2num(getConfigValue(docXml,'ref-index'));   
    ref_ratio = str2num(getConfigValue(docXml,'ref-ratio'));
end

