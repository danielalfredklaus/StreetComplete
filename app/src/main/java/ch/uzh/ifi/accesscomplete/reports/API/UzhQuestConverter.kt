package ch.uzh.ifi.accesscomplete.reports.API


class UzhQuestConverter {
    fun convertToQuest2 (q: UzhQuest): UzhQuest2{
        return UzhQuest2(q.location,q.verifierCount,q.isActive,q.mid,q.title,q.subtitle,q.imageURL,Tags(q.tags!!),q.description,q.updatedby,Verifiers(q.verifiers!!),Histories(q.history!!),q.createdon,q.updatedon,q.changeset,q.version,q.nodeID,q.markerLocation)
    }
}
