function [dl, bcType, bt, gd, sf, ns] = geometry(b)

% A region in b is a connected area of the image, delineated by a number
% of closed loops. The first loop is the outer boundary, the other
% loops, if any, delineate holes in the region. Each loop is
% made of one or more pieces. Pieces are sequences of segments.
% Different pieces may have different boundary conditions:
% pieces away from the image boundaries have Dirichlet conditions, and
% pieces along the image boundaries have Neumann conditions.
% Pieces in b touch at endpoints.

% Determine the number of loops
nregions = length(b);
nloops = 0;
for r = 1:nregions
    region = b{r};
    nloops = nloops + length(region);
end
        
% Determine the size of the geometry description matrix
nPolySegments = zeros(1, nloops);
gcol = 0;
nsegments = 0;
for r = 1:nregions
    region = b{r};
    for l = 1:length(region)
        loop = region{l};
        gcol = gcol + 1;
        nPolySegments(gcol) = 0;
        for q = 1:length(loop)
            piece = loop{q};
            % Skip the last point to leave the loops open and
            % to remove duplicate points between pieces
            nPolySegments(gcol) = nPolySegments(gcol) +  size(piece.points, 2) - 1;
        end
        nsegments = nsegments + nPolySegments(gcol);
    end
end
rows = 2 * max(nPolySegments) + 2;

% Number of digits needed to name all loops
digits = floor(log10(nloops)) + 1;
format = sprintf('P%%0%dd', digits);

% Make description matrices
dl = zeros(7, nsegments);
bcType = char(zeros(1, nsegments));
bt = false(nregions, nloops);
gd = zeros(rows, nloops);
ns = char(zeros(digits+1, nloops));
sf = char(zeros(1, (digits + 2) * nloops - 1));

% All columns of gd represent loops
gd(1, :) = 2;

% All columns of dl represent line edge segments
dl(1, :) = 2;

% The background (label 0) is always on the left in the xy reference
% system. This is what the PDE solver uses, not how regions are
% displayed. Regions are displayed in the ij reference system, in which
% left and right are switched.
dl(6, :) = 0;

% Fill out the matrices
gcol = 0;
dcol = 0;
ss = 1;
for r = 1:nregions
    region = b{r};
    for l = 1:length(region)
        loop = region{l};
        gcol = gcol + 1;
        if l == 1
            bt(r, gcol) = true;
        end
        xs = 3;
        ys = xs + nPolySegments(gcol);
        for q = 1:length(loop)
            piece = loop{q};
            % Skip the last point to leave the loops open and
            % to remove duplicate points between pieces
            sp = piece.points(:, 1:(end-1));
            sl = size(sp, 2);
            for s = 1:sl
                dcol = dcol + 1;
                dl([2 4], dcol) = sp(:, s);
                dl([3 5], dcol) = piece.points(:, s + 1);
                dl(7, dcol) = r;
                bcType(dcol) = piece.type;
            end            
            xe = xs + sl - 1;
            ye = ys + sl - 1;
            gd(xs:xe, gcol) = sp(1, :)';
            gd(ys:ye, gcol) = sp(2, :)';
            xs = xe + 1;
            ys = ye + 1;
        end

        gd(2, gcol) = nPolySegments(gcol);

        name = sprintf(format, gcol);
        ns(:, gcol) = name';
        
        if ss > 1
            if l == 1
                sf(ss) = '+';
            else
                sf(ss) = '-';
            end
            ss = ss + 1;
        end
        se = ss + length(name) - 1;
        sf(ss:se) = name;
        ss = se + 1;

    end
end

if any(csgchk(gd))
    error('Invalid geometry')
end