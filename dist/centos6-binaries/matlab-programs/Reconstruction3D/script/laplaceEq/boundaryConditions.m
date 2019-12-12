function bc = boundaryConditions(dl, bct, img, decimals)

if nargin < 4 || isempty(decimals)
    decimals = 1;
end

cols = size(dl, 2);
if length(bct) ~= cols
    error('Vector bct must have as many entries as dl has columns')
end

% Determine number of characters needed to represent boundary conditions
format = sprintf('%%.%df', decimals);
minString = sprintf(format, min(img(:)));
maxString = sprintf(format, max(img(:)));
numLength = max(length(minString), length(maxString));
numFormat = sprintf('%%%d.%df', numLength, decimals);
dirFormat = sprintf('001%s*(1-s)+%s*s', numFormat, numFormat);
dirLength = length(dirFormat) + 2 * (numLength - length(numFormat));

% Constant parts in Dirichlet and Neumann columns of bc
dirHeader = [1 1 1 1 1 dirLength-3]';
neuCol = [1 0 1 1 double(['0' '0' '0' '0' '1' '0'])]';

% Allocate storage for boundary condition matrix
rows = max(length(neuCol), length(dirHeader) + sum(dirHeader(3:end)));
bc = zeros(rows, cols);

% Dtermine which columns are Dirichlet and which are Neumann
dirichlet = bct == 'd';
neumann = bct == 'n';
nDir = sum(dirichlet);
nNeu = sum(neumann);
oDir = ones(1, nDir);
oNeu = ones(1, nNeu);

% Fill constant parts in Dirichlet and Neumann columns of bc
bc(1:length(dirHeader), dirichlet) = dirHeader * oDir;
bc(1:length(neuCol), neumann) = neuCol * oNeu;

% Complete Dirichlet columns
xa = dl(2, dirichlet);
xb = dl(3, dirichlet);
ya = dl(4, dirichlet);
yb = dl(5, dirichlet);
va = interp2(img, xa, ya);
vb = interp2(img, xb, yb);
str = sprintf(dirFormat, [va; vb]);
bc(length(dirHeader) + (1:dirLength), dirichlet) = reshape(str, dirLength, nDir);