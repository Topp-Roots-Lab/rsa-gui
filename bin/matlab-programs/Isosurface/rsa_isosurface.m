function [] = rsa_isosurface(inFile,outFile)
%
% 
% Integrated into the Duke pipeline by Vladimir Popov, April 19, 2013
% 
% This code calls isosurface_roots function (isosurface_roots.m)
% developed by Joshua Weitz, 2013 - see comments in the isosurface_roots.m
% 
% 
% For integration, the following files should be installed:
% -- this file (rsa_isosurface.m)
% -- isosurface_roots.m
% -- aI.m
%
%

    % display input parameters
    fprintf('%s','inFile=',inFile);
    fprintf('\n');
    fprintf('%s','outFile=',outFile);
    fprintf('\n');

    if ~exist('inFile','var') || isempty(inFile) || ~exist('outFile','var') || isempty(outFile) 
         error('usage: rsa_isosurface(inFile,outFile) with not empty arguments');
    end
    
try   
    fprintf('\n');
    fprintf('%s', 'processing  voxels = ', inFile);
    fprintf('\n');
    
    % calculations for scale=1, scale would be taken into account later.
    scale=1;
    fprintf('%s','scale=1');
    fprintf('\n');

    % need to skip the first two lines
    % Currently, we have in the pipeline
    % -- first line = scale
    % -- second line = number of voxels 
    %   
    % skip 2 rows and zero columns
    rows=2;
    cols=0;
    %
    delimiter='';
    %
    vxls = dlmread(inFile,delimiter,rows,cols);     
    % call isosurface
    fprintf('\n'); 
    fprintf('isosurface_roots STARTS');
    fprintf('\n'); 
    
    fprintf(' ... ... ... ... ... ...');
    fprintf('\n');
    fprintf(' ... ... ... ... ... ...');
    fprintf('\n');
    fprintf(' ... ... ... ... ... ...');
   
    rootstats=isosurface_roots(vxls,scale);
    
    volume=num2str(rootstats.volume);
    area=num2str(rootstats.area);
    fit_area_error=num2str(rootstats.fit_area_error);
    fit_area_error_note=rootstats.fit_area_error_note;
    
    fprintf('\n'); 
    fprintf('isosurface_roots FINISHED');
    fprintf('\n'); 
    
    % display calculations
    fprintf('\n'); 
    fprintf('display isosurface_roots result');
    fprintf('\n'); 
    
    fprintf('\n'); 
    fprintf('%s','rootstats.volume=',volume);
    fprintf('\n');
    fprintf('%s','rootstats.area=',area);
    fprintf('\n');
    fprintf('%s','rootstats.fit_area_error=',fit_area_error);  
    fprintf('\n');  
    fprintf('%s','rootstats.fit_area_error_note=',fit_area_error_note);
    fprintf('\n');    
    
    fprintf('\n'); 
    fprintf('%s','writing calculations result to file: ',outFile);
    fprintf('\n');     

    % Line feed = char(10)  
    newline=char(10);
    info = [
            'voxels=',inFile,newline, ...
            'volume=',num2str(volume),newline, ...
            'area=',num2str(area),newline, ...
            'fit_area_error=',num2str(fit_area_error),newline, ...
            'fit_area_error_note=',fit_area_error_note,newline
           ];
    % write to file   
    dlmwrite(outFile,info,'delimiter',''); 
    
    fprintf('\n'); 
    fprintf('rsa_isosurface DONE');
    fprintf('\n'); 

catch exc
   error('Error occured.Error message: %s.', exc.message);
end

    
end  
