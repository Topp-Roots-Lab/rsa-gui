function [root] = find_largest_component(silImg)

%   constant
dirs = [-1 -1; -1 0; -1 1; 0 -1; 0 1; 1 -1; 1 0; 1 1];

%   initialize
imheight = size(silImg, 1);
imwidth = size(silImg, 2);
visited = false(imheight, imwidth);
nroot = 0;

queue = zeros(numel(silImg), 2); 
nq = 0;
for i = 1:imheight
    for j = 1:imwidth
        if visited(i,j) == false && silImg(i,j) == true
            tmp = false(imheight, imwidth);
            tmp(i,j) = true;
            nq = 1;
            queue(1,:) = [i,j];
            
            %   bfs
            q_link = 0;
            while q_link < nq
                q_link = q_link+1;
                node1 = queue(q_link,:);
                for i = 1:length(dirs)
                    node2 = node1 + dirs(i,:);
                    r = node2(1); c = node2(2);
                    if r>0 && c>0 && r<=imheight && c<=imwidth && silImg(r,c)==true && visited(r,c)==false
                        visited(r,c) = true;
                        tmp(r,c) = true;
                        nq = nq+1;
                        queue(nq,:) = node2;
                    end
                end
            end
            
            if nq > nroot
                root = tmp;
                nroot = nq;
            end
        end
    end
end
