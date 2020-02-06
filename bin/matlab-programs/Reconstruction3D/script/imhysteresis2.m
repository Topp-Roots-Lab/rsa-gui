function [silImg] = imhysteresis3(rootImg, upper_th, lower_th, outfile)

%   constant
dirs = [-1 -1; -1 0; -1 1; 0 -1; 0 1; 1 -1; 1 0; 1 1];

%   initialize
imheight = size(rootImg, 1);
imwidth = size(rootImg, 2);
ids = find(rootImg > upper_th);
silImg = false(imheight, imwidth);
silImg(ids) = 1;
queue = zeros(numel(rootImg), 2);
[r,c] = ind2sub(size(rootImg), ids);
nq = length(r);
queue(1:nq,:) = [r c];

%   bfs
q_link = 0;
while q_link < nq
    q_link = q_link+1;
    node1 = queue(q_link,:);
    for i = 1:length(dirs)
        node2 = node1 + dirs(i,:);
        r = node2(1); c = node2(2);
        if r>0 && c>0 && r<=imheight && c<=imwidth && silImg(r,c)==false && rootImg(r,c)>lower_th(r,c)
            silImg(r,c) = true;
            nq = nq+1;
            queue(nq,:) = node2;
        end
    end
end

%   write result
if nargin == 4
    imwrite(silImg, outfile);
end
