function drawBoundaries(bd, in, arrows, dots)

if nargin < 3 || isempty(arrows)
    arrows = false;
end

if nargin < 4 || isempty(dots)
    dots = false;
end

[rows, cols] = size(in);

clf

if dots
    corner = [1 cols cols 1; 1 1 rows rows];
    [i, j] = find(in);
    plot(corner(1, :), corner(2, :), '.', 'MarkerSize', 1)
    plot(j, i, '.r', 'MarkerSize', 15);
    axis ij
else
    imagesc(in)
end

hold on
drawnow

for k = 1:length(bd)
    drawBoundary(bd{k});
end

if dots
    grey = 0.3 * [1 1 1];
    set(gca, 'Color', grey);
end

figure(gcf)

    function drawBoundary(b)
        for l = 1:length(b)
            loop = b{l};
            arrow = arrows;
            for p = 1:length(loop)
                piece = loop{p};
                switch piece.type
                    case 'd'
                        color = 'y';
                    case 'n'
                        color = 'g';
                    otherwise
                        error('Unknown piece type %s', piece.type)
                end
                start = 1;
                if color == 'y'
                    if arrow
                        arrow = false;
                        start = 2;
                        style = sprintf('.%c', color);
                        plot(piece.points(1, 1), piece.points(2, 1), style);
                        [ax, ay] = dsxy2figxy(gca, piece.points(1, 1:2), ...
                            rows + 1 - piece.points(2, 1:2));
                        a = annotation(gcf, 'arrow', ax, ay);
                        set(a, 'Color', 'y');
                    end
                end
                plot(piece.points(1, start:end), piece.points(2, start:end), color);
            end
        end
    end

end