local o1, f1, f2 = ...
local t, j = {}, 1
for i=0, o1:size()-1 do
    local o2 = f1(o1:get(i))
    if o2 then
        for k=0, o2:size()-1 do
            t[j] = f2(o2:get(k))
            j = j + 1
        end
    end
end
return t