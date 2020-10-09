local fields = {
    ["release"] = setRelease,
    ["title"] = setTitle,
    ["link"] = setLink,
    ["order"] = setOrder
}

return function(t)
    local o = _NovelChapter()
    if not t then return o end

    for k, v in pairs(t) do
        if fields[k] then fields[k](o, v) end
    end

    return o
end
