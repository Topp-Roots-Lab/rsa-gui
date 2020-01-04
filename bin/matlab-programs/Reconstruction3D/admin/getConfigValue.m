function value = getConfigValue( docXml, tagName )
    % 
    % Error, if any, is caught in the main procedure - rootwork
    %
    ListItems = docXml.getElementsByTagName(tagName);
    thisListItem = ListItems.item(0);
    childNode = thisListItem.getFirstChild;
    value = char(childNode.getData);
%     fprintf('%s=%s',tagName,value);
%     fprintf('\n');
end

