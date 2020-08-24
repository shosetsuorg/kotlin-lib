local o, f = ...
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