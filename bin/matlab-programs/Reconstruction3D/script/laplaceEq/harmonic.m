% Compute a piecewise harmonic function h, equal to img at points where the
% Laplacian of img is zero.
% If two output arguments are present in the function call, d is set to a
% data structure that preserves intermediate results for display purposes.

% sigma is the smoothing parameter used in the Laplacian, (defaults to 2).
% hmax is an upper bound on triangulation size (defaults to sigma).
% verbose turns progrss messages on and off (defaults to true).


function [h d w] = harmonic(img, w, hmax, verbose)

tic;

if nargin < 3 || isempty(hmax)
    % Upper bound on the length of the triangulation edges
    hmax = sigma;
end

if nargin < 4 || isempty(verbose)
    verbose = true;
end

if nargout > 1
    keepData = true;
else
    keepData = false;
end

in2 = ones(size(img));
in2 = in2.* (1 - w);

%lap = laplacian(img, sigma);

% Coefficients for the Laplace equation
c = 1;
a = 0;
f = 0;

% Image grid coordinates
[rows, cols] = size(img);
x = 1:cols;
y = 1:rows;

% Initialize harmonic image
h = NaN(rows, cols);

% Split the image into parts with nonnegative and negative Laplacian
%in2 = cat(3, lap >= 0, lap < 0);


part = {'nonnegative', 'negative'};

% If requested, make intermediate data available
if keepData
    %d.laplacian = lap;
    d.pde = repmat(struct('part', {}, 'input', {}, 'boundaries', {}, ...
        'points', {}, 'edges', {}, 'triangles', {}, 'solution', {}), 1, 2);
end

% Process each of the two image parts separately
for i = 1:1
    %tell('Processing the part of the image with %s Laplacian:', part{i});
    
    %in = squeeze(in2(:, :, i));
    in = in2;
    
%     tell('\tComputing problem geometry and boundary conditions')
    b = boundaries(in);
    [dl bcType] = geometry(b);
    bc = boundaryConditions(dl, bcType, img);
    
%     tell('\tTriangulating the domain')
    [p, e, t] = initmesh(dl, 'Hmax', hmax);
    
%     tell('\tSolving the Laplace equation')
    u = assempde(bc, p, e, t, c, a, f);
    
    ugrid = tri2grid(p, t, u, x, y);
    valid = ~isnan(ugrid);
    h(valid) = ugrid(valid);
    
    if keepData
        d.pde{i}.part = part{i};
        d.pde{i}.input = in;
        d.pde{i}.boundaries = b;
        d.pde{i}.points = p;
        d.pde{i}.edges = e;
        d.pde{i}.triangles = t;
        d.pde{i}.solution = u;
    end
end

time = toc;
% tell('Processing time %.2f seconds', time);

    function tell(format, varargin)
        if verbose
            format = [format '\n'];
            fprintf(1, format, varargin{:});
        end
    end

end