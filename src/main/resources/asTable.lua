-- o is a table or java object with size and get methods (usually Elements or ArrayList)
return function(o)
    local t = {} -- result
    if type(o) == "table" then
        for k,v in pairs(o) do
            t[k] = v
        end
    else
        for i=0, o:size()-1 do
            t[i+1] = o:get(i)
        end
    end

    return t
end