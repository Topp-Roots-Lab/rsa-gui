function bd = boundaries(in)

[rows, cols] = size(in);

[label, m] = bwlabel(in, 4);
bd = cell(1, m);
for k = 1:m
    region = double(label == k);
    bd{k} = boundary(region, rows, cols);
end

    function loop = boundary(region, rows, cols)
        
        corner = [1 cols cols 1; 1 1 rows rows];
        
        d = contourc(region, 1/2 * [1 1]);
        kp = 0;
        kl = 0;
        endpoint = [];
        pieceNumber = [];
        while ~isempty(d)
            % Next contour loop
            last = d(2, 1) + 1;
            c = d(:, 2:last);
            d = d(:, (last + 1):end);
            
            if any(c(:, 1) ~= c(:, end))    % Open contour element
                kp = kp + 1;
                endpoint = [endpoint c(:, [1 end])];
                pieceNumber = [pieceNumber [kp kp; 1 size(c, 2)]];
                piece{kp}.points = c; %#ok<*AGROW>
                piece{kp}.lengths = segmentLengths(c);
                piece{kp}.type = 'd';
            else
                kl = kl + 1;
                loop{kl}{1}.points = c;
                loop{kl}{1}.lengths = segmentLengths(c);
                loop{kl}{1}.type = 'd';
            end
        end
        
        if isempty(endpoint)
            % Could it be that the whole image boundary encompasses the region?
            if region(1, 1)
                kl = kl + 1;
                segment = [1 1; 1 cols; rows cols; rows 1; 1 1]';
                loop{kl}{1}.points = segment;
                loop{kl}{1}.lengths = segmentLengths(segment);
                loop{kl}{1}.type = 'n';
            end
        else
            % New loop
            kl = kl + 1;
            
            % Add corners inside region
            for j = 1:4
                if region(corner(2, j), corner(1, j))
                    endpoint = [endpoint corner(:, j)];
                    pieceNumber = [pieceNumber [0; 0]];
                end
            end
            
            % Sort endpoints and corners counterclockwise (in 'axis ij' reference)
            center = [cols, rows] / 2;
            theta = -atan2(endpoint(2, :) - center(2), endpoint(1, :) - center(1));
            [theta order] = sort(theta);
            endpoint = endpoint(:, order);
            pieceNumber = pieceNumber(:, order);
            
            % Close the loop
            endpoint = [endpoint endpoint(:, 1)];
            pieceNumber = [pieceNumber pieceNumber(:, 1)];
            
            % Piece the last loop together
            nmax = size(endpoint, 2) - 1;
            traversed = false(1, max(pieceNumber(1, :)));
            for n = 1:nmax
                pcn = pieceNumber(:, n + [0 1]);
                if ~any(pcn(1, :) == 0) && pcn(1, 1) == pcn(1, 2) && ~traversed(pcn(1, 1))
                    % Add a Dirichlet piece
                    traversed(pcn(1, 1)) = true;
                    if pcn(2, 1) > pcn(2, 2)
                        segment = piece{pcn(1, 1)}.points(:, end:-1:1);
                    else
                        segment = piece{pcn(1, 1)}.points;
                    end
                    type = 'd';
                else % Add a Neumann piece
                    segment = endpoint(:, n + [0 1]);
                    type = 'n';
                end
                loop{kl}{n}.points = segment;
                loop{kl}{n}.lengths = segmentLengths(segment);
                loop{kl}{n}.type = type;
            end
        end
        
        % Orient all loops so that the interior of the region is on the
        % left side while walking along the boundary in the positive direction
        % [Note: 'left' is in the 'axis ij' reference system]
        for kl = 1:length(loop)
            if interiorOnRight(loop{kl}{1}.points(:, 1:2))
                for kp = 1:length(loop{kl})
                    loop{kl}{kp}.points = loop{kl}{kp}.points(:, end:-1:1);
                    loop{kl}{kp}.lengths = loop{kl}{kp}.lengths(:, end:-1:1);
                end
            end
        end
        
        % Make the last loop (outer boundary) first
        if length(loop) > 1
            loop = loop([end 1:(end-1)]);
        end
        
        function len = segmentLengths(segment)
            len = sqrt(sum((segment(:, 2:end) - segment(:, 1:(end-1))) .^ 2, 1));
        end
        
        function answer = interiorOnRight(comp)
            a = comp(:, 1);
            b = comp(:, 2);
            mid = (a + b) / 2;
            dif = b - a;
            dif = dif / norm(dif);
            inside = mid + [-dif(2); dif(1)] / 4;
            answer = interp2(region, inside(1), inside(2)) > 1/2;
        end
    end

end