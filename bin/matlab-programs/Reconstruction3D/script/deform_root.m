function [min_c, min_p, ret, cur_boundary] = deform_root(root1, root2)

if sum(root1(:)-root2(:)<0) > 0
    disp(['input error: root1 should contain root2!']);
    return;
end

%   constant
dirs = [-1 -1; -1 0; -1 1; 0 -1; 0 1; 1 -1; 1 0; 1 1];

%   initialize
imheight = size(root2, 1);
imwidth = size(root2, 2);
ids = find(root2);
min_c = Inf(imheight,imwidth);
min_c(ids) = 0;
min_p = zeros(imheight,imwidth,2);
queue = zeros(numel(root2), 2);
[r,c] = ind2sub(size(root2), ids);
nq = length(r);
queue(1:nq,:) = [r c];

%   bfs
q_link = 0;
while q_link < nq
    q_link = q_link+1;
    node1 = queue(q_link,:);
    cost = min_c(node1(1),node1(2))+1;
    for i = 1:length(dirs)
        node2 = node1 + dirs(i,:);
        r = node2(1); c = node2(2);
        if r>0 && c>0 && r<=imheight && c<=imwidth && root1(r,c)==1 && cost<min_c(r,c)
            min_c(r,c) = cost;
            min_p(r,c,:) = node1;
            nq = nq+1;
            queue(nq,:) = node2;
        end
    end
end
clear queue nq;

%   get boundaries
B = bwboundaries(root1,'noholes');
boundary1 = false(imheight,imwidth);
for k = 1:length(B)
    boundary = B{k};
    for i = 1:length(boundary)
        boundary1(boundary(i,1),boundary(i,2)) = true;
    end
end

B = bwboundaries(root2,'noholes');
boundary2 = false(imheight,imwidth);
for k = 1:length(B)
    boundary = B{k};
    for i = 1:length(boundary)
        boundary2(boundary(i,1),boundary(i,2)) = true;
    end
end

%   deform
cur_boundary = boundary1;
imagesc(cur_boundary);
while (true)
    [y,x] = ginput(1);
    [r,c] = find(cur_boundary);
    if isempty(r) break; end
    cur_boundary = false(imheight,imwidth);
    for i = 1:length(r)
        row = r(i);
        col = c(i);
        if min_p(row,col,1)==0 
            cur_boundary(row,col) = true;
        else
            cur_boundary(min_p(row,col,1),min_p(row,col,2)) = true;
        end
    end
    imagesc(cur_boundary);
end
