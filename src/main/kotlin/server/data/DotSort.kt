package surik.simyan.locdots.server.data

enum class DotSort(
    val value: String,
) {
    PostDistance("Post Distance"),
    PostDate("Post Date"),
    ;

    companion object {
        fun toSortType(sortingType: String): DotSort =
            when (sortingType) {
                PostDate.value -> PostDate
                else -> PostDistance
            }
    }
}
