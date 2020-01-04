function [filename] = get_filename(fileprefix, fileno, fileext,rotation_digits)
    if rotation_digits == 2    
        if fileno < 10
            filename = [fileprefix '0' num2str(fileno) fileext];
        else
            filename = [fileprefix num2str(fileno) fileext];
        end
    elseif rotation_digits == 3 
        if fileno < 10
            filename = [fileprefix '0' '0' num2str(fileno) fileext];
        elseif fileno < 100
            filename = [fileprefix '0' num2str(fileno) fileext];      
        else
            filename = [fileprefix num2str(fileno) fileext];
        end
    else
          msg= ['Only two or three digits allowed for rotation number.' ...
                'Current setting for the number of digits' ' '  ...
                'rotation_digits=' rotation_digits];
          error(msg);
    end
end
    
