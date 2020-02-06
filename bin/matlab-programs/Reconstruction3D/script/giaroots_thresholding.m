% this function substitutes non positive values with 1
function [silImg] = giaroots_thresholding(img, outfile)

    %   initialize
    imheight = size(img, 1);
    imwidth = size(img, 2);
    % giaroots thresholding images are suppossed to be already black and white
    ids = img > 0;
    silImg = false(imheight, imwidth);
    silImg(ids) = 1;

    %   write result
    imwrite(silImg, outfile);
