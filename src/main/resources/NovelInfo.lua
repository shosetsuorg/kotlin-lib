return function(t)
    local o = _NovelChapter()
    if not t then return o end

    local fields = {
        ["title"] = o.setTitle,
        ["alternativeTitles"] = o.setAlternativeTitles,
        ["imageURL"] = o.setImageURL,
        ["language"] = o.setLanguage,
        ["description"] = o.setDescription,
        ["status"] = o.setStatus,
        ["tags"] = o.setTags,
        ["genres"] = o.setGenres,
        ["authors"] = o.setAuthors,
        ["artists"] = o.setArtists,
        ["chapters"] = o.setChapters
    }

    for k, v in pairs(t) do
        if fields[k] then fields[k](o, v) end
    end

    return o
end
