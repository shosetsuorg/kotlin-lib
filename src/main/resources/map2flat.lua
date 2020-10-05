local function map(o, f)
    local t = {}
    if type(o) == "table" then
        for k,v in pairs(o) do
            t[k] = f(v,k)
        end
    else
        for i=0, o:size()-1 do
            t[i+1] = f(o:get(i), i)
        end
    end
    return t
end

do
    local o, f1, f2 = ...
    local t, j = {}, 0
    map(map(o, function(v, k) return f1(v, k) end),
            function(v, k)
                j = j + 1
                t[j] = f2(v, k)
            end)

    return t
end